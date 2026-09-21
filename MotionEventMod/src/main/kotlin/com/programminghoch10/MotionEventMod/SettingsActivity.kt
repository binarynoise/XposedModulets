package com.programminghoch10.MotionEventMod

import kotlin.sequences.forEach
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import androidx.fragment.app.FragmentActivity
import androidx.preference.EditTextPreference
import androidx.preference.MultiSelectListPreferenceDialogFragmentCompat
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
        finish()
        return true
    }
    
    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            preferenceManager.sharedPreferencesName = SHARED_PREFERENCES_NAME
            preferenceManager.sharedPreferencesMode = MODE_WORLD_READABLE
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
            
            val disableTouchTimeoutPreference = findPreference<EditTextPreference>("disableTouchTimeout")!!
            val disableTouchDuringPenPreference = findPreference<SwitchPreference>("disableTouchDuringPen")!!
            val disableTouchDuringHoverPreference = findPreference<SwitchPreference>("disableTouchDuringHover")!!
            val disableTypesPreference = findPreference<Preference>("disableTypes")!!
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
            disableTouchTimeoutPreference.setOnBindEditTextListener {
                it.hint = "Time in seconds"
                it.inputType = InputType.TYPE_CLASS_NUMBER
            }
            disableTypesPreference.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                parentFragmentManager.beginTransaction().add(R.id.settings, TypeSelectorFragment()).commit() > 0
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
            TODO("Not yet implemented")
        }
    }
}

private val TwoStatePreference.isEnabledAndChecked get() = isEnabled && isChecked
