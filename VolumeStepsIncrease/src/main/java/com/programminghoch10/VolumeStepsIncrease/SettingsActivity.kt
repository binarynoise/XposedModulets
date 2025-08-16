package com.programminghoch10.VolumeStepsIncrease

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SeekBarPreference
import com.programminghoch10.VolumeStepsIncrease.Common.Companion.defaultStreamValues
import com.programminghoch10.VolumeStepsIncrease.Common.Companion.getMaxVolumeSteps
import com.programminghoch10.VolumeStepsIncrease.Common.Companion.streams

class SettingsActivity : FragmentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(R.id.settings, SettingsFragment()).commit()
        }
        actionBar?.setDisplayHomeAsUpEnabled(
            supportFragmentManager.backStackEntryCount > 0
        )
    }
    
    class SettingsFragment : PreferenceFragmentCompat() {
        @SuppressLint("WorldReadableFiles")
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
            preferenceManager.sharedPreferencesName = "streams"
            preferenceManager.sharedPreferencesMode = MODE_WORLD_READABLE
            
            for (stream in streams) {
                val defaultValue = defaultStreamValues[stream]!!
                val preference = SeekBarPreference(requireContext())
                preference.key = stream
                preference.title = stream
                preference.min = 1
                preference.max = getMaxVolumeSteps()
                preference.setDefaultValue(defaultValue)
                preference.showSeekBarValue = true
                //preference.summary = "default = ${defaultValue / 2}"
                preferenceScreen.addPreference(preference)
            }
        }
    }
}
