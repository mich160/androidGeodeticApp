package student.pl.edu.pb.geodeticapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import student.pl.edu.pb.geodeticapp.R;
import student.pl.edu.pb.geodeticapp.data.entities.GeoPoint;
import student.pl.edu.pb.geodeticapp.geoutils.CS2000RefSystemPicker;
import student.pl.edu.pb.geodeticapp.geoutils.Coordinates2D;
import student.pl.edu.pb.geodeticapp.geoutils.ReferenceSystemConverter;
import student.pl.edu.pb.geodeticapp.geoutils.exceptions.ReferenceSystemConversionException;

public class EditPointActivity extends BaseActivity{
    public static final int CREATE_MODE = 0, EDIT_MODE = 1;
    public static final String PARAMETER_KEY = "geo_point_param", RESULT_KEY = "geo_point_result", MODE_KEY = "point_mode";

    private int currentMode;
    private ReferenceSystemConverter converter;
    private GeoPoint pointData;
    private SparseArray<ReferenceSystemConverter.ReferenceSystem> intToReferenceSystemMap;
    private Map<ReferenceSystemConverter.ReferenceSystem, Integer> referenceSystemToIntMap;

    private EditText nameEditText;
    private EditText latitudeEditText;
    private EditText longitudeEditText;
    private EditText xGKEditText;
    private EditText yGKEditText;
    private Spinner cs2000zoneSpinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_point);

        nameEditText = (EditText) findViewById(R.id.point_name_edit_text);
        latitudeEditText = (EditText) findViewById(R.id.latitude_edit_text);
        longitudeEditText = (EditText) findViewById(R.id.longitude_edit_text);
        xGKEditText = (EditText) findViewById(R.id.xgk_edit_text);
        yGKEditText = (EditText) findViewById(R.id.ygk_edit_text);
        cs2000zoneSpinner = (Spinner) findViewById(R.id.cs2000_zone_spinner);

        initZonesHandling();
        initListeners();

        currentMode = getIntent().getIntExtra(MODE_KEY, -1);
        if(currentMode == CREATE_MODE){
            pointData = new GeoPoint();
        }
        else if(currentMode == EDIT_MODE){
            pointData = (GeoPoint) getIntent().getSerializableExtra(PARAMETER_KEY);
            loadData();
        }
        converter = new ReferenceSystemConverter();
    }

    private void initZonesHandling(){
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.cs2000_zones_array, android.R.layout.simple_spinner_item);
        cs2000zoneSpinner.setAdapter(spinnerAdapter);
        intToReferenceSystemMap = new SparseArray<>();
        referenceSystemToIntMap = new HashMap<>();
        intToReferenceSystemMap.put(0, ReferenceSystemConverter.ReferenceSystem.CS2000z5);
        intToReferenceSystemMap.put(1, ReferenceSystemConverter.ReferenceSystem.CS2000z6);
        intToReferenceSystemMap.put(2, ReferenceSystemConverter.ReferenceSystem.CS2000z7);
        intToReferenceSystemMap.put(3, ReferenceSystemConverter.ReferenceSystem.CS2000z8);
        referenceSystemToIntMap.put(ReferenceSystemConverter.ReferenceSystem.CS2000z5, 0);
        referenceSystemToIntMap.put(ReferenceSystemConverter.ReferenceSystem.CS2000z6, 1);
        referenceSystemToIntMap.put(ReferenceSystemConverter.ReferenceSystem.CS2000z7, 2);
        referenceSystemToIntMap.put(ReferenceSystemConverter.ReferenceSystem.CS2000z8, 3);
    }

    private void initListeners(){

    }

    private void loadData() {
        nameEditText.setText(pointData.getName());
        latitudeEditText.setText(String.format(Locale.ENGLISH, "%f", pointData.getLatitude()));
        longitudeEditText.setText(String.format(Locale.ENGLISH, "%f", pointData.getLongitude()));
        xGKEditText.setText(String.format(Locale.ENGLISH, "%f", pointData.getxGK()));
        yGKEditText.setText(String.format(Locale.ENGLISH, "%f", pointData.getyGK()));
    }

    private boolean applyData(){
        boolean stepOneValidation = validateName();
        stepOneValidation = stepOneValidation && validateSpinner();
        if (stepOneValidation){
            if (validateWSG84()){
                convertToCS2000();
                return true;
            }
            else if(validateCS2000()){
                convertToWSG84();
                return true;
            }
        }
        return false;
    }

    private boolean validateName(){
        if(nameEditText.getText().toString().equals("")){
            nameEditText.setError(getString(R.string.name_validate_error));
            return false;
        }
        nameEditText.setError(null);
        return true;
    }

    private boolean validateSpinner(){
        return cs2000zoneSpinner.getSelectedItemPosition() != AdapterView.INVALID_POSITION;
    }

    private boolean validateWSG84(){
        boolean result = validateDoubleEdit(latitudeEditText);
        if(result){
            result = validateDoubleEdit(longitudeEditText);
        }
        return result;
    }

    private boolean validateCS2000(){
        boolean result = validateDoubleEdit(xGKEditText);
        if (result){
            result = validateDoubleEdit(yGKEditText);
        }
        if(result){
            result = validateSpinner();
        }
        return result;
    }

    private boolean validateDoubleEdit(EditText editText){
        try {
            Double.parseDouble(editText.getText().toString());
            editText.setError(null);
            return true;
        }catch (NumberFormatException e){
            editText.setError(getString(R.string.double_validate_error));
            return false;
        }
    }

    private void convertToCS2000(){
        Coordinates2D coordinates = new Coordinates2D(Double.parseDouble(longitudeEditText.getText().toString()), Double.parseDouble(latitudeEditText.getText().toString()));
        try {
            ReferenceSystemConverter.ReferenceSystem targetSystem = CS2000RefSystemPicker.getReferenceSystem(coordinates.getX());
            cs2000zoneSpinner.setSelection(referenceSystemToIntMap.get(targetSystem));
            Coordinates2D result = converter.convert(coordinates, ReferenceSystemConverter.ReferenceSystem.WSG84,targetSystem);
            xGKEditText.setText(String.format(Locale.ENGLISH, "%f", result.getX()));
            yGKEditText.setText(String.format(Locale.ENGLISH, "%f", result.getY()));
        } catch (ReferenceSystemConversionException e) {
            Log.e("point_conversion", "Couldn't convert point!");
            e.printStackTrace();
        }
    }

    private void convertToWSG84(){
        Coordinates2D coordinates = new Coordinates2D(Double.parseDouble(xGKEditText.getText().toString()), Double.parseDouble(yGKEditText.getText().toString()));
        try {
            ReferenceSystemConverter.ReferenceSystem sourceSystem = intToReferenceSystemMap.get(cs2000zoneSpinner.getSelectedItemPosition());
            Coordinates2D result = converter.convert(coordinates, sourceSystem, ReferenceSystemConverter.ReferenceSystem.WSG84);
            latitudeEditText.setText(String.format(Locale.ENGLISH, "%f", result.getX()));
            longitudeEditText.setText(String.format(Locale.ENGLISH, "%f", result.getY()));
        } catch (ReferenceSystemConversionException e) {
            Log.e("point_conversion", "Couldn't convert point!");
            e.printStackTrace();
        }
    }

    private void saveFieldsToPoint(){
        pointData.setName(nameEditText.getText().toString());
        pointData.setLatitude(Double.parseDouble(latitudeEditText.getText().toString()));
        pointData.setLongitude(Double.parseDouble(longitudeEditText.getText().toString()));
        pointData.setxGK(Double.parseDouble(xGKEditText.getText().toString()));
        pointData.setyGK(Double.parseDouble(yGKEditText.getText().toString()));
    }

    public void onConfirmPointButtonClick(View view) {
        if (applyData()){
            saveFieldsToPoint();
            Intent returnIntent = new Intent();
            returnIntent.putExtra(RESULT_KEY, pointData);
            setResult(RESULT_OK, returnIntent);
            finish();
        }
    }

    public void onConvertToCS2000ButtonClick(View view){
        if(validateWSG84()){
            convertToCS2000();
        }
    }

    public void onConvertToWSG84ButtonClick(View view){
        if(validateCS2000()){
            convertToWSG84();
        }
    }

}
