package com.programminghoch10.SplitScreenMods

import java.util.function.*
import android.content.Context
import android.graphics.Rect
import android.os.Build
import com.programminghoch10.SplitScreenMods.BuildConfig.SHARED_PREFERENCES_NAME
import com.programminghoch10.SplitScreenMods.KeepSwapRatioHookConfig.enabled
import de.binarynoise.logger.Logger.log
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XSharedPreferences
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

object KeepSwapRatioHookConfig {
    @JvmField
    val enabled = Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE
}

class KeepSwapRatioHook : IXposedHookLoadPackage {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName != "com.android.systemui") return
        if (!enabled) return
        log("handleLoadPackage(${lpparam.packageName} in process ${lpparam.processName})")
        val preferences = XSharedPreferences(BuildConfig.APPLICATION_ID, SHARED_PREFERENCES_NAME)
        val enabled = preferences.getBoolean("KeepSwapRatio", false)
        if (!enabled) return
        
        val SplitLayoutClass = XposedHelpers.findClass("com.android.wm.shell.common.split.SplitLayout", lpparam.classLoader)
        val playSwapAnimationMethod =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) SplitLayoutClass.declaredMethods.single { it.name == "playSwapAnimation" }
            else SplitLayoutClass.declaredMethods.single { it.name == "splitSwitching" }
        val getDisplayStableInsetsMethod = XposedHelpers.findMethodExact(SplitLayoutClass, "getDisplayStableInsets", Context::class.java)
        
        XposedBridge.hookMethod(
            playSwapAnimationMethod,
            object : XC_MethodReplacement() {
                override fun replaceHookedMethod(param: MethodHookParam): Any? {
                    @Suppress("UNCHECKED_CAST")
                    val callback = param.args[3] as Consumer<Rect>
                    val context = XposedHelpers.getObjectField(param.thisObject, "mContext") as Context
                    val insets = getDisplayStableInsetsMethod.invoke(param.thisObject, context) as Rect
                    callback.accept(insets)
                    log("replaced ${playSwapAnimationMethod.name} with dummy implementation to prevent swapping")
                    return null
                }
            },
        )
    }
}
