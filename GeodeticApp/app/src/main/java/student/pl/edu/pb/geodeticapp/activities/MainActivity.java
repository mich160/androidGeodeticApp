package student.pl.edu.pb.geodeticapp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import student.pl.edu.pb.geodeticapp.R;
import student.pl.edu.pb.geodeticapp.data.GeoDBHelper;
import student.pl.edu.pb.geodeticapp.data.dao.GeoPointDAO;
import student.pl.edu.pb.geodeticapp.data.entities.GeoPoint;
import student.pl.edu.pb.geodeticapp.geoutils.CS2000RefSystemPicker;
import student.pl.edu.pb.geodeticapp.geoutils.ReferenceSystemConverter;
import student.pl.edu.pb.geodeticapp.views.CompassView;

public class MainActivity extends BaseActivity implements OnMapReadyCallback {

    private class CompassHandler implements SensorEventListener {
        private SensorManager sensorManager;
        private Sensor magneticFieldSensor;
        private Sensor accelerometer;

        private float[] gravityReading;
        private float[] geomagneticReading;

        private CompassView compassView;

        private CompassHandler(CompassView compassView) {
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

        private void start() {
            sensorManager.registerListener(this, magneticFieldSensor, SensorManager.SENSOR_DELAY_GAME);
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        }

        private void stop() {
            sensorManager.unregisterListener(this);
        }
    }

    private final static String URL_FORMAT_STR = "google.navigation:q=%f,%f&mode=w";

    private CompassHandler compassHandler;
    private CompassView compass;
    private SupportMapFragment mapFragment;
    private GoogleMap googleMap;

    private SharedPreferences sharedPreferences;
    private GeoPointDAO geoPointDAO;

    private boolean showWSG84;
    private boolean showCS2000;
    private boolean showCompass;
    private boolean mapLoaded;

    private List<GeoPoint> geoPoints;
    private GeoPoint selectedPoint;
    private HashMap<GeoPoint, Marker> geoPointToMarkerMap;
    private HashMap<String, String> cs2000NameToStringMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initData();
        initViews();
        compassHandler = new CompassHandler(compass);
        loadSettings();
        checkCompassVisibility();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSettings();
        compassHandler.start();
        checkCompassVisibility();
        reloadData();
        if (mapLoaded) {
            reloadMapData();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        compassHandler.stop();
    }

    @Override
    protected void onStop() {
        super.onStop();
        compassHandler.stop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == EditPointActivity.EDIT_MODE) {
            GeoPoint resultPoint = (GeoPoint) data.getSerializableExtra(EditPointActivity.RESULT_KEY);
            updatePointToDB(resultPoint);
            reloadData();
            reloadMapData();
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(resultPoint.getLatitude(), resultPoint.getLongitude())));
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            this.googleMap = googleMap;
        } catch (SecurityException ex) {
            showError(getString(R.string.gps_init_error));
        }
        initMap(googleMap);
        createMarkersForPoints();
        reloadMapData();
    }

    private void initMap(GoogleMap googleMap) {
        googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                LayoutInflater inflater = getLayoutInflater();
                LinearLayout rootLayout = (LinearLayout) inflater.inflate(R.layout.point_info, null);
                TextView pointNameText = (TextView) rootLayout.findViewById(R.id.marker_name_text);
                TextView zoneText = (TextView) rootLayout.findViewById(R.id.marker_zone_text);
                TextView latitudeText = (TextView) rootLayout.findViewById(R.id.marker_latitude_text);
                TextView longitudeText = (TextView) rootLayout.findViewById(R.id.marker_longitude_text);
                TextView xgkText = (TextView) rootLayout.findViewById(R.id.marker_xgk_text);
                TextView ygkText = (TextView) rootLayout.findViewById(R.id.marker_ygk_text);
                pointNameText.setText(((GeoPoint) (marker.getTag())).getName());
                if (showWSG84) {
                    latitudeText.setText(String.format(Locale.getDefault(), getString(R.string.latitude_format_short), ((GeoPoint) marker.getTag()).getLatitude()));
                    longitudeText.setText(String.format(Locale.getDefault(), getString(R.string.longitude_format_short), ((GeoPoint) marker.getTag()).getLongitude()));
                } else {
                    latitudeText.setVisibility(View.GONE);
                    longitudeText.setVisibility(View.GONE);
                }
                if (showCS2000) {
                    zoneText.setText(getZoneString(marker.getPosition().longitude));
                    xgkText.setText(String.format(Locale.getDefault(), getString(R.string.xgk_format_short), ((GeoPoint) marker.getTag()).getxGK()));
                    ygkText.setText(String.format(Locale.getDefault(), getString(R.string.ygk_format_short), ((GeoPoint) marker.getTag()).getyGK()));
                } else {
                    zoneText.setVisibility(View.GONE);
                    xgkText.setVisibility(View.GONE);
                    ygkText.setVisibility(View.GONE);
                }
                return rootLayout;
            }
        });
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (selectedPoint != null) {
                    deselectPoint();
                }
            }
        });
        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (selectedPoint != null) {
                    deselectPoint();
                }
                selectedPoint = (GeoPoint) marker.getTag();
                marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                return false;
            }
        });
        mapLoaded = true;
    }

    private void deselectPoint() {
        Marker selected = geoPointToMarkerMap.get(selectedPoint);
        selected.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        selectedPoint = null;
    }


    private void createMarkersForPoints() {
        for (GeoPoint point : geoPoints) {
            MarkerOptions marker = new MarkerOptions();
            marker.position(new LatLng(point.getLatitude(), point.getLongitude()))
                    .title(point.getName())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            Marker newMarker = googleMap.addMarker(marker);
            newMarker.setTag(point);
            geoPointToMarkerMap.put(point, newMarker);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    public void onEditPointButtonClick(View button) {
        if (selectedPoint != null) {
            Intent newPointIntent = new Intent(this, EditPointActivity.class);
            newPointIntent.putExtra(EditPointActivity.MODE_KEY, EditPointActivity.EDIT_MODE);
            newPointIntent.putExtra(EditPointActivity.PARAMETER_KEY, selectedPoint);
            startActivityForResult(newPointIntent, EditPointActivity.EDIT_MODE);
        } else {
            showError(getString(R.string.point_not_selected_error));
        }
    }

    public void onNavigateToPointButtonClick(View button) {
        if (selectedPoint != null) {
            Uri navigationUri = Uri.parse(String.format(Locale.ENGLISH, URL_FORMAT_STR, selectedPoint.getLatitude(), selectedPoint.getLongitude()));
            Intent navigationIntent = new Intent(Intent.ACTION_VIEW, navigationUri);
            startActivity(navigationIntent);
        } else {
            showError(getString(R.string.point_not_selected_error));
        }
    }

    private void initViews() {
        compass = (CompassView) findViewById(R.id.compass);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.main_map);
        mapFragment.getMapAsync(this);
    }

    private void loadSettings() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        showCompass = sharedPreferences.getBoolean(SettingsActivity.SHOW_COMPASS_KEY, true);
        showCS2000 = sharedPreferences.getBoolean(SettingsActivity.SHOW_CS2000_KEY, true);
        showWSG84 = sharedPreferences.getBoolean(SettingsActivity.SHOW_WSG84_KEY, true);
    }

    private void initData() {
        geoPointDAO = new GeoPointDAO(new GeoDBHelper(getApplicationContext()));
        geoPoints = new ArrayList<>();
        geoPointToMarkerMap = new HashMap<>();
        cs2000NameToStringMap = new HashMap<>();
        cs2000NameToStringMap.put(ReferenceSystemConverter.ReferenceSystem.CS2000z5.getName(), getResources().getStringArray(R.array.cs2000_zones_array)[0]);
        cs2000NameToStringMap.put(ReferenceSystemConverter.ReferenceSystem.CS2000z6.getName(), getResources().getStringArray(R.array.cs2000_zones_array)[1]);
        cs2000NameToStringMap.put(ReferenceSystemConverter.ReferenceSystem.CS2000z7.getName(), getResources().getStringArray(R.array.cs2000_zones_array)[2]);
        cs2000NameToStringMap.put(ReferenceSystemConverter.ReferenceSystem.CS2000z8.getName(), getResources().getStringArray(R.array.cs2000_zones_array)[3]);
        mapLoaded = false;
        reloadData();
    }

    private void reloadData() {
        geoPoints = geoPointDAO.getAll();
    }

    private void reloadMapData() {
        googleMap.clear();
        selectedPoint = null;
        geoPointToMarkerMap.clear();
        createMarkersForPoints();
    }


    private void checkCompassVisibility() {
        if (showCompass) {
            compass.setVisibility(View.VISIBLE);
        } else {
            compass.setVisibility(View.GONE);
        }
    }

    private void updatePointToDB(GeoPoint point) {
        this.geoPointDAO.update(point);
    }

    private void showError(String message) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    private String getZoneString(double longitude) {
        return cs2000NameToStringMap.get(CS2000RefSystemPicker.getReferenceSystem(longitude).getName());
    }

}
