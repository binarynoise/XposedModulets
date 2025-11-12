package com.programminghoch10.SplitScreenMods

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.preference.PreferenceFragmentCompat
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
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            preferenceManager.sharedPreferencesName = SHARED_PREFERENCES_NAME
            preferenceManager.sharedPreferencesMode = Context.MODE_WORLD_READABLE
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
            
            for (preference in preferenceScreen.children) {
                if (!preference.hasKey()) continue
                val clazz = Class.forName(
                    this::class.java.packageName + "." + preference.key + "HookConfig",
                    false,
                    this::class.java.classLoader,
                )
                val enabled = clazz.declaredFields.find { it.name == "enabled" }!!.getBoolean(null)
                preference.isEnabled = enabled
                preference.isVisible = enabled
            }
        }
    }
}
