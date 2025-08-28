package com.programminghoch10.DisableSounds

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceGroup
import androidx.preference.children

val SHARED_PREFERENCES_NAME = "disable_sounds"

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
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
            preferenceScreen.setIconSpaceReservedRecursive(false)
        }
        
        fun Preference.setIconSpaceReservedRecursive(iconSpaceReserved: Boolean) {
            this.isIconSpaceReserved = iconSpaceReserved
            if (this is PreferenceGroup) children.forEach { it.setIconSpaceReservedRecursive(iconSpaceReserved) }
        }
    }
}
