package com.programminghoch10.PreventAudioFocus

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.preference.PreferenceFragmentCompat

class SettingsActivity : FragmentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(R.id.settings, SettingsFragment()).commit()
        }
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }
    
    override fun onNavigateUp(): Boolean {
        finish()
        return true
    }
    
    class SettingsFragment : PreferenceFragmentCompat() {
        @SuppressLint("WorldReadableFiles")
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            preferenceManager.sharedPreferencesName = SHARED_PREFERENCES_NAME
            preferenceManager.sharedPreferencesMode = MODE_WORLD_READABLE
            val context = requireContext()
            preferenceScreen = preferenceManager.createPreferenceScreen(context)
            
            val radioPreferences = mutableListOf<RadioPreference>()
            for (entry in ENTRIES) {
                val preference = RadioPreference(context)
                preference.key = entry.key
                preference.title = entry.key
                preference.setDefaultValue(entry.value == ENTRIES_DEFAULT)
                radioPreferences.add(preference)
                preference.setOnPreferenceChangeListener { preference, newValue ->
                    radioPreferences.forEach { it.onRadioPreferenceSelected(preference as RadioPreference) }
                    true
                }
                preferenceScreen.addPreference(preference)
            }
        }
    }
}
