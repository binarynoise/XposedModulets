package com.programminghoch10.SplitScreenMods

import com.programminghoch10.SplitScreenMods.BuildConfig.SHARED_PREFERENCES_NAME
import com.programminghoch10.SplitScreenMods.FreeSnapHookConfig.enabled
import de.binarynoise.logger.Logger.log
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XSharedPreferences
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

object FreeSnapHookConfig {
    @JvmField
    val enabled = SnapModeHookConfig.enabled
}

class FreeSnapHook : IXposedHookLoadPackage {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName != "com.android.systemui") return
        if (!enabled) return
        log("handleLoadPackage(${lpparam.packageName} in process ${lpparam.processName})")
        val preferences = XSharedPreferences(BuildConfig.APPLICATION_ID, SHARED_PREFERENCES_NAME)
        val enabled = preferences.getBoolean("FreeSnap", false)
        if (!enabled) return
        
        val DividerSnapAlgorithmClass = XposedHelpers.findClass("com.android.wm.shell.common.split.DividerSnapAlgorithm", lpparam.classLoader)
        XposedBridge.hookAllConstructors(DividerSnapAlgorithmClass, object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                XposedHelpers.setBooleanField(param.thisObject, "mFreeSnapMode", true)
                log("${DividerSnapAlgorithmClass.simpleName} mFreeSnapMode constant changed to true")
            }
        })
    }
}
