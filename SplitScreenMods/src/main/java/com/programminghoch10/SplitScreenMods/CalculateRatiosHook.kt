package com.programminghoch10.SplitScreenMods

import com.programminghoch10.SplitScreenMods.BuildConfig.SHARED_PREFERENCES_NAME
import com.programminghoch10.SplitScreenMods.CalculateRatiosHookConfig.enabled
import de.binarynoise.logger.Logger.log
import de.robv.android.xposed.IXposedHookInitPackageResources
import de.robv.android.xposed.XSharedPreferences
import de.robv.android.xposed.callbacks.XC_InitPackageResources

object CalculateRatiosHookConfig {
    val enabled = SnapModeHookConfig.enabled
}

class CalculateRatiosHook : IXposedHookInitPackageResources {
    override fun handleInitPackageResources(resparam: XC_InitPackageResources.InitPackageResourcesParam) {
        if (resparam.packageName != "com.android.systemui") return
        if (!enabled) return
        log("handleInitPackageResources(${resparam.packageName})")
        val preferences = XSharedPreferences(BuildConfig.APPLICATION_ID, SHARED_PREFERENCES_NAME)
        if (!preferences.contains("CalculateRatios")) return
        
        val enabled = preferences.getBoolean("CalculateRatios", false)
        log("set config_flexibleSplitRatios to $enabled")
        resparam.res.setReplacement("android", "bool", "config_flexibleSplitRatios", enabled)
    }
}
