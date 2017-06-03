package student.pl.edu.pb.geodeticapp.fragments;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import student.pl.edu.pb.geodeticapp.R;

public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
