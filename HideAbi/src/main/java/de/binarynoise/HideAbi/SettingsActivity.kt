package de.binarynoise.HideAbi

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.preference.CheckBoxPreference
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceGroup
import androidx.preference.children
import de.binarynoise.HideAbi.BuildConfig.SHARED_PREFERENCES_NAME
import de.binarynoise.reflection.cast


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
        
        
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            preferenceManager.sharedPreferencesName = SHARED_PREFERENCES_NAME
            preferenceManager.sharedPreferencesMode = MODE_WORLD_READABLE
            
            val ctx = preferenceManager.context
            preferenceScreen = preferenceManager.createPreferenceScreen(ctx)
            preferenceScreen.apply {
                for (category in AbiCategory.entries) {
                    addPreference(PreferenceCategory(ctx)) {
                        title = category.name
                        val abis = ctx.getHardwareABIs(category)
                        for (abi in abis) {
                            addPreference(CheckBoxPreference(ctx)) {
                                key = category.getPreferenceKeyFor(abi)
                                title = abi
                                summaryOn = "This ABI will be hidden and not reported to apps"
                                summaryOff = "This ABI will be reported to apps"
                            }
                        }
                    }
                }
                setIconSpaceReservedRecursive(false)
            }
        }
        
        /**
         * Returns the hardware ABIs for the given property.
         * Combines the ABIs we currently see with the ones that are hooked from the preferences (and can't see).
         */
        fun Context.getHardwareABIs(property: AbiCategory): Set<String> {
            val sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_WORLD_READABLE)
            
            return (property.getter() + sharedPreferences.all //
                .filter { (k, v) -> k.startsWith(property.property) && (v?.cast<Boolean?>() == true) } //
                .keys //
                .map { it.substringAfter("_") } //
                .toTypedArray()) //
                .toSet()
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
