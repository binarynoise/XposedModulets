package com.programminghoch10.SplitScreenMods

import android.content.res.XResources
import android.os.Build
import android.util.TypedValue
import com.programminghoch10.SplitScreenMods.BuildConfig.SHARED_PREFERENCES_NAME
import com.programminghoch10.SplitScreenMods.RemoveMinimalTaskSizeHookConfig.enabled
import de.binarynoise.logger.Logger.log
import de.robv.android.xposed.IXposedHookInitPackageResources
import de.robv.android.xposed.XSharedPreferences
import de.robv.android.xposed.callbacks.XC_InitPackageResources

object RemoveMinimalTaskSizeHookConfig {
    @JvmField
    val enabled = Build.VERSION.SDK_INT >= Build.VERSION_CODES.BAKLAVA
}

class RemoveMinimalTaskSizeHook : IXposedHookInitPackageResources {
    override fun handleInitPackageResources(resparam: XC_InitPackageResources.InitPackageResourcesParam) {
        if (resparam.packageName != "com.android.systemui") return
        if (!enabled) return
        log("handleInitPackageResources(${resparam.packageName})")
        val preferences = XSharedPreferences(BuildConfig.APPLICATION_ID, SHARED_PREFERENCES_NAME)
        val enabled = preferences.getBoolean("RemoveMinimalTaskSize", false)
        if (!enabled) return
        
        resparam.res.setReplacement(
            "android",
            "dimen",
            "default_minimal_size_resizable_task",
            object : XResources.DimensionReplacement(0f, TypedValue.COMPLEX_UNIT_DIP) {},
        )
        log("set replacement for default_minimal_size_resizable_task")
    }
}
