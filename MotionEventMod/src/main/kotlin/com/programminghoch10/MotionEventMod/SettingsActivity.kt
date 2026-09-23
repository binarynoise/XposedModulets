package com.programminghoch10.MotionEventMod

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceGroup
import androidx.preference.SwitchPreference
import androidx.preference.TwoStatePreference
import androidx.preference.children
import com.programminghoch10.MotionEventMod.BuildConfig.SHARED_PREFERENCES_NAME

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
        if (supportFragmentManager.backStackEntryCount > 0) supportFragmentManager.popBackStack()
        else finish()
        return true
    }
    
    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            preferenceManager.sharedPreferencesName = SHARED_PREFERENCES_NAME
            preferenceManager.sharedPreferencesMode = MODE_WORLD_READABLE
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
            
            val disableTouchTimeoutPreference = findPreference<TimeoutPreference>("disableTouchTimeout")!!
            val disableTouchDuringPenPreference = findPreference<SwitchPreference>("disableTouchDuringPen")!!
            val disableTouchDuringHoverPreference = findPreference<SwitchPreference>("disableTouchDuringHover")!!
            val testPreference = findPreference<Preference>("test")!!
            
            fun recalculateDependencies() {
                disableTouchTimeoutPreference.isEnabled = disableTouchDuringPenPreference.isChecked || disableTouchDuringHoverPreference.isChecked
            }
            recalculateDependencies()
            listOf(disableTouchDuringPenPreference, disableTouchDuringHoverPreference).forEach {
                it.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, _ ->
                    recalculateDependencies()
                    true
                }
            }
            testPreference.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                val intent = Intent(context, InputTestActivity::class.java)
                requireActivity().startActivity(intent)
                true
            }
        }
    }
    
    class TypeSelectorFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            preferenceManager.sharedPreferencesName = SHARED_PREFERENCES_NAME
            preferenceManager.sharedPreferencesMode = MODE_WORLD_READABLE
            preferenceScreen = preferenceManager.createPreferenceScreen(requireContext())
            toolTypes.forEach {
                val preference = SwitchPreference(requireContext())
                preference.key = toolTypeEnabledKey(it)
                preference.title = it
                preference.setDefaultValue(true)
                preferenceScreen.addPreference(preference)
            }
        }
    }
}

private val TwoStatePreference.isEnabledAndChecked get() = isEnabled && isChecked
