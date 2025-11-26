package com.programminghoch10.SplitScreenMods

import android.animation.Animator
import android.os.Build
import com.programminghoch10.SplitScreenMods.BuildConfig.SHARED_PREFERENCES_NAME
import com.programminghoch10.SplitScreenMods.DisableSwapAnimationHookConfig.enabled
import de.binarynoise.logger.Logger.log
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XSharedPreferences
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

object DisableSwapAnimationHookConfig {
    @JvmField
    val enabled = Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM
}

class DisableSwapAnimationHook : IXposedHookLoadPackage {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName != "com.android.systemui") return
        if (!enabled) return
        log("handleLoadPackage(${lpparam.packageName} in process ${lpparam.processName})")
        val preferences = XSharedPreferences(BuildConfig.APPLICATION_ID, SHARED_PREFERENCES_NAME)
        val enabled = preferences.getBoolean("DisableSwapAnimation", false)
        if (!enabled) return
        
        val SplitLayoutClass = XposedHelpers.findClass("com.android.wm.shell.common.split.SplitLayout", lpparam.classLoader)
        val playSwapAnimationMethod = SplitLayoutClass.declaredMethods.single { it.name == "playSwapAnimation" }
        
        XposedBridge.hookMethod(playSwapAnimationMethod, object : XC_MethodHook(PRIORITY_LOWEST) {
            override fun afterHookedMethod(param: MethodHookParam) {
                log("skipping scheduled swap animation")
                val mSwapAnimator = XposedHelpers.getObjectField(param.thisObject, "mSwapAnimator") as Animator
                mSwapAnimator.end()
            }
        })
    }
}
