package elbainteraction.hostiletakeover;

import android.content.Context;
import android.content.Intent;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MainMapActivity extends FragmentActivity implements LocationListener {

    private static final long MIN_TIME = 40;
    private static final float MIN_DISTANCE = 40;
    private static final int VIBRATION_TIME = 50;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private GameInstance gameInstance;
    private LocationManager locationManager;
    private ProgressShaker progressShaker;
    private ProgressBar progressBar;
    private Vibrator vibrator;
    private SensorManager mSensorManager;
    private Button takeoverButton;
    private boolean firstOnResume;
    private Button startTakeOverButton;
    private MediaPlayer takeOverSound;


    //Methods related to te activity
    /**Creates the game. If the intent comes from new game, set up a new game. If the intent comes from the continue game screen, use the parameters provided from the intent (loaded from the database).
     * Also initiates the map, the shakelisteners for taking over a tile, the sound for taking over a tile .
     * Lastly inititates the game and downloads the tiles from the database.*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.firstOnResume = true;
        setContentView(R.layout.activity_main_map);
        setUpMapIfNeeded();
        GameInstanceFactory gameInstanceFactory = new GameInstanceFactory();
        takeOverSound =  MediaPlayer.create(this,R.raw.trumpet_takeover);

        Intent intent = getIntent();
        //If the intent comes from the new game screen.
        if (intent.getStringExtra("gameType").equals("newGame")) {
            Location location = getLocation();
            //If a location can be found.
            if (location != null) {
                LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
            gameInstance = gameInstanceFactory.createGameInsteance(intent.getStringExtra("gameName"),
                    intent.getIntExtra("numberOfTeams", 0),location.getLatitude(),location.getLongitude(), intent.getStringExtra("mapSize"), intent.getIntExtra("gameTime", 0), mMap);
            gameInstance.initiateGame(this);
            gameInstance.setTeamColor("red");}

        }
        //Else if the intent comes from the continue game screen
        else {
            //gameInstance = gameInstanceFactory.createGameInsteance(intent.getStringExtra("teamName"), mMap);

            gameInstance = new GameInstance(intent.getStringExtra("gameName"),intent.getDoubleExtra("startLatitude",0),
                    intent.getDoubleExtra("startLongitude",0), 4, intent.getIntExtra("rowCount",0));
            gameInstance.setGoogleMap(mMap);
            gameInstance.initiateGame(this);
            gameInstance.setTeamColor(intent.getStringExtra("teamColor"));


        }
        //Creates a handler that periodically checks for database updates.
        final Handler handler = new Handler();
        final Runnable r = new Runnable() {
            public void run() {
                new UpdateTiles(gameInstance).executeOnExecutor(Executors.newFixedThreadPool(2),null);
                handler.postDelayed(this, 10000);
            }
        };
        handler.post(r);



        if (mMap != null) {
            mMap.setMyLocationEnabled(true);
            mMap.animateCamera(CameraUpdateFactory.zoomTo(10));

            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, this); //You can also use LocationManager.GPS_PROVIDER and LocationManager.PASSIVE_PROVIDER

            this.takeoverButton = (Button) this.findViewById(R.id.takeoverButton);
            this.startTakeOverButton = (Button) this.findViewById(R.id.startTakeoverButton);

            //progressssskit
            progressBar = (ProgressBar) this.findViewById(R.id.takeOverProgress);
            progressBar.setMax(12);

            vibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
            mSensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
            progressShaker = new ProgressShaker(progressBar, vibrator, mSensorManager, takeoverButton, (TextView) findViewById(R.id.shake_text_prompt), getApplicationContext());

            centerCamera(getLocation());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        progressShaker.progressShakerPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        if (!firstOnResume) {
            progressShaker.progressShakerResume();
        } else {
            firstOnResume = false;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            startActivity(new Intent(this, MainMenuActivity.class));
            this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    /*Methods related to setting up the map.*/
    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
    }

    /*Methods used for taking over a zone*/
    /**Shows the progress bar that is otherwise invisible in the UI.
     * This happends when the Take Over Zone button is pressed to inititate the takeover.*/
    public void showProgressBar(View v) {
        Location location = getLocation();
        //If a location can be found.
        if (location != null) {
            LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
            if (progressBar.getVisibility() == View.INVISIBLE && takeoverButton.getVisibility() == View.INVISIBLE){
                if(gameInstance.findTile(userLocation)!= null) {
                    vibrator.vibrate(VIBRATION_TIME);
                    startTakeOverButton.setVisibility(View.INVISIBLE);
                    findViewById(R.id.shake_text_prompt).setVisibility(View.VISIBLE);
                    progressShaker.setBarVisible();
                    progressShaker.progressShakerResume();

                }
                else{
                    Toast.makeText(this,"Outside tile boundaries! No tile to capture.",Toast.LENGTH_LONG);}
            }
    }

    }

    /**Method for taking over a zone. Triggered when the shake is done and the Finish Takeover button is pressed.
     * Hides Finish Takeover button, shows the Start Takeover Button.
     * If a current location can be found, the game is told to change the current owner of the tile and update the database.
     * Plays a sound when capture is successfull.*/
    public void takeOverZone(View v) {
        vibrator.vibrate(VIBRATION_TIME);
        takeoverButton.setVisibility(View.INVISIBLE);
        startTakeOverButton.setVisibility(View.VISIBLE);

        Location location = getLocation();
        //If a location can be found.
        if (location != null) {
            LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());

            if(gameInstance.changeTileTeam(userLocation)){
                takeOverSound.start();
            }
            else{
                Toast.makeText(getApplicationContext(), "Outside tile boundaries! No tile captured.",
                        Toast.LENGTH_LONG).show();
            }

        //If a location cannot be found.
        } else {
            Toast.makeText(getApplicationContext(), "Error recieving location from Google!",
                    Toast.LENGTH_LONG).show();
        }
    }


    /**Method for centering the camera on the users current location. Used when a game is set up or joined.*/
    private void centerCamera(Location location){
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
            mMap.animateCamera(cameraUpdate);
            locationManager.removeUpdates(this);

    }

    /**Provides an accurate location for the user. Uses GPS or Network provider to find the best location.
     *  Much more reliable than the googlemaps lastKnownLocation. But also less battery and data efficient.
     * */
    public Location getLocation() {
        boolean isGPSEnabled;
        boolean isNetworkEnabled;
        Location location = null;
        try {
            locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
            } else {

                double latitude;
                double longitude;
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME,
                            MIN_DISTANCE, this);
                    Log.d("Network", "Network Enabled");
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME,
                                MIN_DISTANCE, this);
                        Log.d("GPS", "GPS Enabled");
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }

    @Override
    public void onLocationChanged(Location location) {
        locationManager.removeUpdates(this);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


}
