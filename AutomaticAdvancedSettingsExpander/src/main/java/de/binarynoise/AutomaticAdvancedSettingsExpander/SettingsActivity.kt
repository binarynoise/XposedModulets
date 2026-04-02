package de.binarynoise.AutomaticAdvancedSettingsExpander

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceGroup
import androidx.preference.SwitchPreference
import androidx.preference.children
import de.binarynoise.AutomaticAdvancedSettingsExpander.BuildConfig.SHARED_PREFERENCES_NAME

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAndRemoveTask()
        }
        return true
    }
    
    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            preferenceManager.sharedPreferencesName = SHARED_PREFERENCES_NAME
            preferenceManager.sharedPreferencesMode = MODE_WORLD_READABLE
            
            val ctx = preferenceManager.context
            preferenceScreen = preferenceManager.createPreferenceScreen(ctx)
            preferenceScreen.apply {
                addPreference(SwitchPreference(ctx)) {
                    key = "PreferenceGroupChildrenHook"
                    title = "Automatically show all children in PreferenceGroups"
                    setDefaultValue(true)
                    summary = "Found quite often in the Settings on older Androids"
                }
                
                addPreference(SwitchPreference(ctx)) {
                    key = "ExpandablePreferenceHook"
                    title = "Automatically expand ExpandablePreferences"
                    val canUse = Build.VERSION.SDK_INT >= Build.VERSION_CODES.BAKLAVA
                    setDefaultValue(canUse)
                    isEnabled = canUse
                    summary = "Found in newer Androids (16+) in the Notification Settings"
                }
                
                setIconSpaceReservedRecursive(false)
            }
        }
        
        
        @OptIn(ExperimentalContracts::class)
        inline fun <T : Preference> PreferenceGroup.addPreference(preference: T, setup: T.() -> Unit) {
            contract {
                callsInPlace(setup, InvocationKind.EXACTLY_ONCE)
            }
            
            val isPreferenceGroup = preference is PreferenceGroup
            
            if (isPreferenceGroup) {
                // PreferenceGroup needs to be added to the tree before other preferences can be added to it
                addPreference(preference)
            }
            
            preference.apply(setup)
            
            if (!isPreferenceGroup) {
                // normal preferences need the setup applied before being added to the tree
                addPreference(preference)
            }
        }
        
        private fun Preference.setIconSpaceReservedRecursive(iconSpaceReserved: Boolean = false) {
            this.isIconSpaceReserved = iconSpaceReserved
            if (this is PreferenceGroup) this.children.forEach { it.setIconSpaceReservedRecursive(iconSpaceReserved) }
        }
    }
}
