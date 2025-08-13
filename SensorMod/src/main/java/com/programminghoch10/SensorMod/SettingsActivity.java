package com.programminghoch10.SensorMod;

import static com.programminghoch10.SensorMod.Common.DEFAULT_SENSOR_STATE;
import static com.programminghoch10.SensorMod.Common.getKey;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;

import java.util.List;

public class SettingsActivity extends FragmentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(
                    getSupportFragmentManager().getBackStackEntryCount() > 0
            );
        }
    }
    
    public static class SettingsFragment extends PreferenceFragmentCompat {
        @SuppressLint("WorldReadableFiles")
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            PreferenceManager preferenceManager = getPreferenceManager();
            setPreferenceScreen(preferenceManager.createPreferenceScreen(requireContext()));
            preferenceManager.setSharedPreferencesName("sensors");
            preferenceManager.setSharedPreferencesMode(MODE_WORLD_READABLE);
            PreferenceScreen preferenceScreen = getPreferenceScreen();
            SensorManager sensorManager = (SensorManager) requireContext().getSystemService(SENSOR_SERVICE);
            List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ALL);
            for (Sensor sensor : sensors) {
                SwitchPreference preference = new SwitchPreference(requireContext());
                preference.setDefaultValue(DEFAULT_SENSOR_STATE);
                preference.setKey(getKey(sensor));
                preference.setTitle(sensor.getName());
                preference.setSummary(getKey(sensor) + " - " + sensor);
                preferenceScreen.addPreference(preference);
            }
        }
    }
}
