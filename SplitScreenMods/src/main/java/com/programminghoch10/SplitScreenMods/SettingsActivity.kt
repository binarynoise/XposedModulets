package com.programminghoch10.SplitScreenMods

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SeekBarPreference
import androidx.preference.SwitchPreference
import androidx.preference.children
import com.programminghoch10.SplitScreenMods.BuildConfig.SHARED_PREFERENCES_NAME

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
        
        lateinit var onSharedPreferencesChangedListener: SharedPreferences.OnSharedPreferenceChangeListener
        
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            preferenceManager.sharedPreferencesName = SHARED_PREFERENCES_NAME
            preferenceManager.sharedPreferencesMode = MODE_WORLD_READABLE
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
            
            val alwaysAllowMultiInstanceSplitPreference = preferenceScreen.findPreference<SwitchPreference>("AlwaysAllowMultiInstanceSplit")!!
            val keepSplitScreenRatioPreference = preferenceScreen.findPreference<SwitchPreference>("KeepSplitScreenRatio")!!
            val keepSwapRatioPreference = preferenceScreen.findPreference<SwitchPreference>("KeepSwapRatio")!!
            val disableSwapAnimationPreference = preferenceScreen.findPreference<SwitchPreference>("DisableSwapAnimation")!!
            val snapModePreference = preferenceScreen.findPreference<ListPreference>("SnapMode")!!
            val freeSnapPreference = preferenceScreen.findPreference<SwitchPreference>("FreeSnap")!!
            val snapTargetsPreference = preferenceScreen.findPreference<ListPreference>("SnapTargets")!!
            val customRatioPreference = preferenceScreen.findPreference<SeekBarPreference>("CustomRatio")!!
            val calculateRatiosPreference = preferenceScreen.findPreference<SwitchPreference>("CalculateRatios")!!
            val removeMinimalTaskSizePreference = preferenceScreen.findPreference<SwitchPreference>("RemoveMinimalTaskSize")!!
            
            fun calculateDependencies() {
                alwaysAllowMultiInstanceSplitPreference.setEnabledAndVisible(AlwaysAllowMultiInstanceSplitHookConfig.enabled)
                keepSplitScreenRatioPreference.setEnabledAndVisible(KeepSplitScreenRatioHookConfig.enabled)
                keepSwapRatioPreference.setEnabledAndVisible(KeepSwapRatioHookConfig.enabled)
                disableSwapAnimationPreference.setEnabledAndVisible(DisableSwapAnimationHookConfig.enabled || keepSwapRatioPreference.isEnabledAndChecked)
                snapModePreference.setEnabledAndVisible(SnapModeHookConfig.enabled)
                val is1_1SnapMode = snapModePreference.value == SNAP_MODE.SNAP_ONLY_1_1.key
                val isFixedRatioSnapMode = snapModePreference.value == SNAP_MODE.SNAP_FIXED_RATIO.key
                freeSnapPreference.setEnabledAndVisible(FreeSnapHookConfig.enabled && snapModePreference.isEnabled && !is1_1SnapMode)
                with(snapTargetsPreference) {
                    setEnabledAndVisible(snapModePreference.isEnabled && CustomFixedRatioHookConfig.enabled && isFixedRatioSnapMode)
                    setEntries(
                        when {
                            !AdditionalSnapTargetsHookConfig.enabled -> R.array.CUSTOM_ONLY_SNAP_TARGET_TITLES
                            freeSnapPreference.isChecked -> R.array.SINGLE_SNAP_TARGET_TITLES
                            else -> R.array.SNAP_TARGET_TITLES
                        }
                    )
                    setEntryValues(
                        when {
                            !AdditionalSnapTargetsHookConfig.enabled -> R.array.CUSTOM_ONLY_SNAP_TARGET_KEYS
                            freeSnapPreference.isChecked -> R.array.SINGLE_SNAP_TARGET_KEYS
                            else -> R.array.SNAP_TARGET_KEYS
                        }
                    )
                }
                customRatioPreference.setEnabledAndVisible(snapTargetsPreference.isEnabled && snapTargetsPreference.value == "CUSTOM")
                removeMinimalTaskSizePreference.setEnabledAndVisible(RemoveMinimalTaskSizeHookConfig.enabled && !is1_1SnapMode)
                calculateRatiosPreference.setEnabledAndVisible(CalculateRatiosHookConfig.enabled && snapModePreference.isEnabled && !removeMinimalTaskSizePreference.isEnabledAndChecked && isFixedRatioSnapMode)
                
                preferenceScreen.children.filterIsInstance<PreferenceCategory>().forEach { preferenceCategory ->
                    preferenceCategory.isVisible = preferenceCategory.children.any { it.isEnabled }
                }
            }
            
            onSharedPreferencesChangedListener = SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
                calculateDependencies()
            }
            preferenceManager.sharedPreferences!!.registerOnSharedPreferenceChangeListener(onSharedPreferencesChangedListener)
            
            calculateDependencies()
        }
    }
}

val SwitchPreference.isEnabledAndChecked: Boolean get() = this.isEnabled && this.isChecked

fun Preference.setEnabledAndVisible(enabled: Boolean) {
    this.isEnabled = enabled
    this.isVisible = enabled
}
