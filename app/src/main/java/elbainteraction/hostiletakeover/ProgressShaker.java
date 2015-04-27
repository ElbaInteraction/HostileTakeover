package elbainteraction.hostiletakeover;


import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;

import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ProgressShaker {

    private SensorManager mSensorManager;
    private ShakeListener mSensorListener;
    private int timesShaken;
    private Vibrator v;
    private ProgressBar progressBar;
    private Button takeoverButton;
    private TextView shakeText;


    public ProgressShaker(final ProgressBar progressBar, final Vibrator v, SensorManager mSensorManager, final Button takeoverButton, final TextView shakeText, final Context context){

      this.takeoverButton = takeoverButton;
      this.progressBar = progressBar;
      this.v = v;
      this.mSensorManager = mSensorManager;

        mSensorListener = new ShakeListener();
        mSensorListener.setOnShakeListener(new ShakeListener.OnShakeListener() {


            public void onShake() {
                timesShaken++;
                progressBar.setProgress(timesShaken);

                //Check if finished -> vibrate
                if(timesShaken == 12) {
                    // Vibrate for 500 milliseconds
                    v.vibrate(500);
                    shakeText.setVisibility(View.INVISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                    progressShakerPause();
                    timesShaken = 0;
                    takeoverButton.setVisibility(View.VISIBLE);

                    try {
                        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                        Ringtone r = RingtoneManager.getRingtone(context, notification);
                        r.play();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        );
        progressShakerPause();
    }

    protected void progressShakerResume() {
        mSensorManager.registerListener(mSensorListener,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_UI);
    }

    protected void progressShakerPause() {
        mSensorManager.unregisterListener(mSensorListener);
    }

    public void setBarVisible() {

            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(0);
            progressShakerResume();

    }
}

