package student.pl.edu.pb.geodeticapp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;

import java.util.List;

import student.pl.edu.pb.geodeticapp.R;
import student.pl.edu.pb.geodeticapp.data.GeoDBHelper;
import student.pl.edu.pb.geodeticapp.data.dao.GeoPointDAO;
import student.pl.edu.pb.geodeticapp.data.entities.GeoPoint;
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

    private class GPSHandler implements LocationListener{

        public GPSHandler(){

        }

        @Override
        public void onLocationChanged(Location location) {

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

        private void start(){

        }

        private void stop(){

        }
    }

    private CompassHandler compassHandler;
    private CompassView compass;

    private SharedPreferences sharedPreferences;
    private GeoPointDAO geoPointDAO;

    private boolean showWSG84;
    private boolean showCS2000;
    private boolean showCompass;

    private List<GeoPoint> geoPoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initData();
        compassHandler = new CompassHandler(compass);
        loadSettings();
        checkCompassVisibility();
        reloadData();
    }

    private void initViews(){
        compass = (CompassView) findViewById(R.id.compass);
    }

    @Override
    protected void onResume(){
        super.onResume();
        loadSettings();
        compassHandler.start();
        checkCompassVisibility();
        reloadData();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //TODO gdy punkt jest edytowany
    }

    private void loadSettings(){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        showCompass = sharedPreferences.getBoolean(SettingsActivity.SHOW_COMPASS_KEY, true);
        showCS2000 = sharedPreferences.getBoolean(SettingsActivity.SHOW_CS2000_KEY, true);
        showWSG84 = sharedPreferences.getBoolean(SettingsActivity.SHOW_WSG84_KEY, true);
    }

    private void initData(){
        geoPointDAO = new GeoPointDAO(new GeoDBHelper(getApplicationContext()));
    }

    private void reloadData(){
        geoPoints = geoPointDAO.getAll();
        drawPoints();
        //TODO wyswietlanie
    }

    private void drawPoints() {

    }

    private void drawPoint(GeoPoint point){

    }

    private void drawCurrentPosition(){

    }

    private void checkCompassVisibility(){
        if(showCompass){
            compass.setVisibility(View.VISIBLE);
        }
        else {
            compass.setVisibility(View.GONE);
        }
    }

}
