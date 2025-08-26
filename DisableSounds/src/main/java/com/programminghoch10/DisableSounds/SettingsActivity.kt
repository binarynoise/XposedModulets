package com.programminghoch10.DisableSounds

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.preference.PreferenceFragmentCompat
import com.programminghoch10.DisableSounds.BuildConfig.SHARED_PREFERENCES_NAME

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
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            preferenceManager.sharedPreferencesName = SHARED_PREFERENCES_NAME
            preferenceManager.sharedPreferencesMode = MODE_WORLD_READABLE
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
        }
    }
}
