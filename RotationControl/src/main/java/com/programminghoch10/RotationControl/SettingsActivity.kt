package com.programminghoch10.RotationControl

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceGroup
import androidx.preference.children

val ROTATION_MODE_DEFAULT = ROTATION_MODE.SCREEN_ORIENTATION_SENSOR
const val SHARED_PREFERENCES_NAME = "rotation_mode"

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
        finishAndRemoveTask()
        return true
    }
    
    class SettingsFragment : PreferenceFragmentCompat() {
        val radioPreferences: MutableList<RadioPreference> = mutableListOf()
        
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            preferenceManager.sharedPreferencesName = SHARED_PREFERENCES_NAME
            preferenceManager.sharedPreferencesMode = MODE_WORLD_READABLE
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
            val preferenceCategory = findPreference<PreferenceCategory>("category_rotation_mode")!!
            val context = requireContext()
            
            for (rotationMode in ROTATION_MODE.entries) {
                val preference = RadioPreference(context)
                preference.key = rotationMode.key
                preference.title = rotationMode.title
                preference.summary = rotationMode.summary
                preference.setDefaultValue(rotationMode == ROTATION_MODE_DEFAULT)
                radioPreferences.add(preference)
                preference.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { preference, newValue ->
                    radioPreferences.forEach { it.onRadioPreferenceSelected(preference as RadioPreference) }
                    true
                }
                preferenceCategory.addPreference(preference)
                if (rotationMode in rewriteLockedOrientation.keys) preference.dependency = "rewrite_locked_orientations"
                if (rotationMode in rewriteSensorOrientation.keys) preference.dependency = "rewrite_sensor_orientations"
            }
            
            preferenceScreen.setIconSpaceReservedRecursive()
        }
        
        private fun Preference.setIconSpaceReservedRecursive(iconSpaceReserved: Boolean = false) {
            this.isIconSpaceReserved = iconSpaceReserved
            if (this is PreferenceGroup) this.children.forEach { it.setIconSpaceReservedRecursive(iconSpaceReserved) }
        }
    }
}
