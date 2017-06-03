package student.pl.edu.pb.geodeticapp.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CheckBox;

import student.pl.edu.pb.geodeticapp.R;
import student.pl.edu.pb.geodeticapp.fragments.SettingsFragment;

public class SettingsActivity extends BaseActivity {
    public static final String SHOW_COMPASS_KEY = "pref_key_show_compass";
    public static final String SHOW_WSG84_KEY = "pref_key_show_wsg84";
    public static final String SHOW_CS2000_KEY = "pref_key_show_cs2000";

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }


    @Override
    protected void onPause(){
        super.onPause();
    }

    @Override
    protected void onStop(){
        super.onStop();
    }

}
