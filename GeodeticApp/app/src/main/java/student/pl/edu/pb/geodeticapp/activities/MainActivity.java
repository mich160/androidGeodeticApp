package student.pl.edu.pb.geodeticapp.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;

import student.pl.edu.pb.geodeticapp.R;
import student.pl.edu.pb.geodeticapp.views.CompassView;

public class MainActivity extends BaseActivity{

    private class CompassHandler implements SensorEventListener{
        private SensorManager sensorManager;
        private Sensor magneticFieldSensor;
        private Sensor accelerometer;

        private float[] gravityReading;
        private float[] geomagneticReading;

        private CompassView compassView;

        private CompassHandler(CompassView compassView){
            sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
            magneticFieldSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            gravityReading = new float[3];
            geomagneticReading = new float[3];

            this.compassView = compassView;
            start();
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            final float alpha = 0.97f;

            synchronized (this) {
                if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

                    gravityReading[0] = alpha * gravityReading[0] + (1 - alpha)
                            * event.values[0];
                    gravityReading[1] = alpha * gravityReading[1] + (1 - alpha)
                            * event.values[1];
                    gravityReading[2] = alpha * gravityReading[2] + (1 - alpha)
                            * event.values[2];
                }

                if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {

                    geomagneticReading[0] = alpha * geomagneticReading[0] + (1 - alpha)
                            * event.values[0];
                    geomagneticReading[1] = alpha * geomagneticReading[1] + (1 - alpha)
                            * event.values[1];
                    geomagneticReading[2] = alpha * geomagneticReading[2] + (1 - alpha)
                            * event.values[2];

                }

                float R[] = new float[9];
                float I[] = new float[9];
                boolean success = SensorManager.getRotationMatrix(R, I, gravityReading,
                        geomagneticReading);
                if (success) {
                    float orientation[] = new float[3];
                    SensorManager.getOrientation(R, orientation);
                    float azimuth = (float) Math.toDegrees(orientation[0]);
                    azimuth = (azimuth + 360) % 360;
                    this.compassView.update(azimuth);
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        private void start(){
            sensorManager.registerListener(this, magneticFieldSensor, SensorManager.SENSOR_DELAY_GAME);
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        }

        private void stop(){
            sensorManager.unregisterListener(this);
        }
    }

    private CompassHandler compassHandler;

    private CompassView compass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
//        checkSettings();
        compassHandler = new CompassHandler(compass);
    }

    private void initViews(){
        compass = (CompassView) findViewById(R.id.compass);
    }

    @Override
    protected void onResume(){
        super.onResume();
//        checkSettings();
        compassHandler.start();
    }

    @Override
    protected void onPause(){
        super.onPause();
        compassHandler.stop();
    }

    @Override
    protected void onStop(){
        super.onStop();
        compassHandler.stop();
    }

//    private void checkSettings(){
//        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
//        if(!sharedPreferences.contains(SettingsActivity.INITIALIZED_KEY)){
//            SharedPreferences.Editor spEditor = sharedPreferences.edit();
//            spEditor.putBoolean(SettingsActivity.SHOW_COMPASS_KEY, SettingsActivity.SHOW_COMPASS_DEFAULT);
//            spEditor.putBoolean(SettingsActivity.SHOW_WSG84_KEY, SettingsActivity.SHOW_WSG84_DEFAULT);
//            spEditor.putBoolean(SettingsActivity.SHOW_GS2000_KEY, SettingsActivity.SHOW_GS2000_DEFAULT);
//            spEditor.putBoolean(SettingsActivity.INITIALIZED_KEY, true);
//            spEditor.commit();
//        }
//        if(sharedPreferences.contains(SettingsActivity.INITIALIZED_KEY)){
//            if(sharedPreferences.getBoolean(SettingsActivity.SHOW_COMPASS_KEY, true)){
//                compass.setVisibility(View.VISIBLE);
//            }
//            else {
//                compass.setVisibility(View.GONE);
//            }
//        }
//    }

}
