package student.pl.edu.pb.geodeticapp.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CheckBox;

import student.pl.edu.pb.geodeticapp.R;

public class SettingsActivity extends BaseActivity {
    public static final boolean SHOW_COMPASS_DEFAULT = true;
    public static final boolean SHOW_WSG84_DEFAULT = true;
    public static final boolean SHOW_GS2000_DEFAULT = true;
    public static final String INITIALIZED_KEY = "initialized";
    public static final String SHOW_COMPASS_KEY = "showCompass";
    public static final String SHOW_WSG84_KEY = "showWSG84";
    public static final String SHOW_GS2000_KEY = "showGS2000";

    private SharedPreferences sharedPreferences;

    private CheckBox showCompassCheckbox;
    private CheckBox showWSG84CheckBox;
    private CheckBox showGS2000CheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        showCompassCheckbox = (CheckBox) findViewById(R.id.show_compass_checkbox);
        showWSG84CheckBox = (CheckBox) findViewById(R.id.show_wsg84_checkbox);
        showGS2000CheckBox = (CheckBox) findViewById(R.id.show_gs2000_checkbox);
//        loadSettings();
    }


    @Override
    protected void onPause(){
        super.onPause();
//        saveSettings();
    }

    @Override
    protected void onStop(){
        super.onStop();
//        saveSettings();
    }


//    private void loadSettings(){
//        sharedPreferences = getPreferences(Context.MODE_PRIVATE);
//        showCompassCheckbox.setChecked(sharedPreferences.getBoolean(SHOW_COMPASS_KEY, true));
//        showGS2000CheckBox.setChecked(sharedPreferences.getBoolean(SHOW_GS2000_KEY, true));
//        showWSG84CheckBox.setChecked(sharedPreferences.getBoolean(SHOW_WSG84_KEY, true));
//    }
//
//    private void saveSettings(){
//        SharedPreferences.Editor spEditor = sharedPreferences.edit();
//        spEditor.putBoolean(SHOW_COMPASS_KEY, showCompassCheckbox.isChecked());
//        spEditor.putBoolean(SHOW_GS2000_KEY, showCompassCheckbox.isChecked());
//        spEditor.putBoolean(SHOW_WSG84_KEY, showCompassCheckbox.isChecked());
//        spEditor.commit();
//    }
}
