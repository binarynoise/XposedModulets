package com.programminghoch10.SplitScreenMods

import android.os.Build
import com.programminghoch10.SplitScreenMods.BuildConfig.SHARED_PREFERENCES_NAME
import com.programminghoch10.SplitScreenMods.SnapModeHookConfig.enabled
import de.binarynoise.logger.Logger.log
import de.robv.android.xposed.IXposedHookInitPackageResources
import de.robv.android.xposed.XSharedPreferences
import de.robv.android.xposed.callbacks.XC_InitPackageResources

object SnapModeHookConfig {
    @JvmField
    val enabled = Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE
}

class SnapModeHook : IXposedHookInitPackageResources {
    override fun handleInitPackageResources(resparam: XC_InitPackageResources.InitPackageResourcesParam) {
        if (resparam.packageName != "com.android.systemui") return
        if (!enabled) return
        log("handleInitPackageResources(${resparam.packageName})")
        val preferences = XSharedPreferences(BuildConfig.APPLICATION_ID, SHARED_PREFERENCES_NAME)
        val selectedSnapMode = preferences.getString("SnapMode", "SYSTEM")
        if (selectedSnapMode == "SYSTEM") return
        
        val selectedSnapModeId = SNAP_MODE.entries.find { it.key == selectedSnapMode }!!.value
        resparam.res.setReplacement("android", "integer", "config_dockedStackDividerSnapMode", selectedSnapModeId)
        log("overwriting SnapMode with $selectedSnapMode aka $selectedSnapModeId")
    }
}
