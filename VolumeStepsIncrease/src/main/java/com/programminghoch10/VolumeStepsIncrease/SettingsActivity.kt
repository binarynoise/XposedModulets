package com.programminghoch10.VolumeStepsIncrease

import kotlin.math.round
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceGroup
import androidx.preference.SeekBarPreference
import androidx.preference.children
import com.programminghoch10.VolumeStepsIncrease.Common.SHARED_PREFERENCES_NAME
import com.programminghoch10.VolumeStepsIncrease.Common.STREAMS
import com.programminghoch10.VolumeStepsIncrease.Common.STREAM_NAMES
import com.programminghoch10.VolumeStepsIncrease.Common.getModuleDefaultVolumeSteps
import com.programminghoch10.VolumeStepsIncrease.Common.getModuleMaxVolumeSteps
import com.programminghoch10.VolumeStepsIncrease.Common.getModuleMinVolumeSteps
import com.programminghoch10.VolumeStepsIncrease.Common.getPreferenceKey
import com.programminghoch10.VolumeStepsIncrease.Common.getSystemMaxVolumeSteps

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
            preferenceManager.sharedPreferencesName = SHARED_PREFERENCES_NAME
            preferenceManager.sharedPreferencesMode = MODE_WORLD_READABLE
            
            for (stream in STREAM_NAMES) {
                val streamInt = STREAMS[stream]!!
                val preference = SeekBarPreference(requireContext())
                preference.key = getPreferenceKey(streamInt)
                preference.title = stream.replace("STREAM_", "")
                preference.min = getModuleMinVolumeSteps(streamInt)
                preference.max = getModuleMaxVolumeSteps(streamInt)
                preference.setDefaultValue(getModuleDefaultVolumeSteps(streamInt))
                preference.showSeekBarValue = true
                preference.updatesContinuously = true
                preference.setOnPreferenceChangeListener { preference, newValue ->
                    val factor = (newValue as Int).toDouble() / getSystemMaxVolumeSteps(streamInt)
                    preference.summary = arrayOf(
                        "factor=${factor.round(1)}x ",
                        //"systemMin=${getSystemMinVolumeSteps(streamInt)} ",
                        "systemMax=${getSystemMaxVolumeSteps(streamInt)} ",
                    ).joinToString(" ")
                    true
                }
                preferenceScreen.addPreference(preference)
                preference.onPreferenceChangeListener?.onPreferenceChange(preference, preference.value)
            }
            preferenceScreen.setIconSpaceReservedRecursive(false)
        }
        
        fun Preference.setIconSpaceReservedRecursive(iconSpaceReserved: Boolean) {
            this.isIconSpaceReserved = iconSpaceReserved
            if (this is PreferenceGroup) children.forEach { it.setIconSpaceReservedRecursive(iconSpaceReserved) }
        }
        
        fun Double.round(decimals: Int = 0): Double {
            var multiplier = 1.0
            repeat(decimals) { multiplier *= 10 }
            return round(this * multiplier) / multiplier
        }
    }
}
