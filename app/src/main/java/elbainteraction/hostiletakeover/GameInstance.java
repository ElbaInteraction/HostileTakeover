package elbainteraction.hostiletakeover;

import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolygonOptions;

/**
 * Created by Henrik on 2015-04-09.
 */
public class GameInstance implements Runnable{
    public static final double LUND_MAP_X_START_POINT = 55.719763;
    public static final double LUND_MAP_Y_START_POINT = 13.184195;
    public static final Team NO_TEAM = new Team(Color.TRANSPARENT);
    private double overlayStartPointX;
    private double overlayStartPointY;
    private double overlayWidthX;
    private double overlayWidthY;
    private int numberOfRows;
    protected GameTile[][] gameTiles;
    int numberOfTeams;
    GoogleMap mMap;


    public GameInstance(int numberOfTeams,GoogleMap mMap, double overlayStartPointX, double overlayStartPointY,
                        double overlayWidthX, double overlayWidthY, int numberOfRows){
        this.mMap = mMap;
        this.numberOfTeams = numberOfTeams;
        this.overlayStartPointX = overlayStartPointX;
        this.overlayStartPointY = overlayStartPointY;
        this.overlayWidthX = overlayWidthX;
        this.overlayWidthY = overlayWidthY;
        this.numberOfRows = numberOfRows;
        gameTiles = new GameTile[numberOfRows][numberOfRows];

    }

    /**
     * Initiates the runnable thread to start the game  **/
    public void initiateGame(){
        initiateOverlay();
        Thread thread = new Thread(this);
        thread.start();

    }

    /**
     * Method for initiating the square tiles dividing the map in different zones.  **/
    private void initiateOverlay() {

        for(int i = 0; i<numberOfRows; i++){
            for(int j = 0; j<numberOfRows; j++){

                gameTiles[i][j] = new GameTile(overlayStartPointX - overlayWidthX * i,overlayStartPointY + overlayWidthY * j, overlayWidthX, overlayWidthY, NO_TEAM);
                PolygonOptions rectangle = new PolygonOptions().add(
                        new LatLng( overlayStartPointX - overlayWidthX * i      ,    overlayStartPointY + overlayWidthY * j),
                        new LatLng( overlayStartPointX - overlayWidthX *(i+1)   ,    overlayStartPointY + overlayWidthY * j),
                        new LatLng( overlayStartPointX - overlayWidthX *(i+1)   ,    overlayStartPointY + overlayWidthY *(j+1)),
                        new LatLng( overlayStartPointX - overlayWidthX * i      ,    overlayStartPointY + overlayWidthY *(j+1)))
                        .fillColor(Color.TRANSPARENT)
                        .strokeWidth(1);
                mMap.addPolygon(rectangle);
            }

        }



    }
    public void changeTileColor(LatLng userLocation){
        for(int i = 0; i<numberOfRows; i++){
            for(int j = 0; j<numberOfRows; j++){
                if(gameTiles[i][j].locationInTile(userLocation)){

                    PolygonOptions rectangle = new PolygonOptions().add(
                            new LatLng( gameTiles[i][j].getX()                                    ,    gameTiles[i][j].getY()),
                            new LatLng( gameTiles[i][j].getX()  - gameTiles[i][j].getWidthX()     ,    gameTiles[i][j].getY()),
                            new LatLng( gameTiles[i][j].getX()  - gameTiles[i][j].getWidthX()     ,    gameTiles[i][j].getY() + gameTiles[i][j].getWidthY()),
                            new LatLng( gameTiles[i][j].getX()                                    ,    gameTiles[i][j].getY() + gameTiles[i][j].getWidthY()))
                            .fillColor(Color.RED)
                            .strokeWidth(1);
                    mMap.addPolygon(rectangle);
                    return;
                }
            }

        }

    }


    @Override
    public void run() {

    }
}
