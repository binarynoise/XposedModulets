package com.programminghoch10.SplitScreenMods

import android.content.ComponentName
import android.content.pm.ActivityInfo
import android.os.Build
import com.programminghoch10.SplitScreenMods.AlwaysAllowMultiInstanceSplitHookConfig.enabled
import com.programminghoch10.SplitScreenMods.BuildConfig.SHARED_PREFERENCES_NAME
import de.binarynoise.logger.Logger.log
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XSharedPreferences
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

object AlwaysAllowMultiInstanceSplitHookConfig {
    @JvmField
    val enabled = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
}

class AlwaysAllowMultiInstanceSplitHook : IXposedHookLoadPackage {
    
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (!enabled) return
        log("handleLoadPackage(${lpparam.packageName} in process ${lpparam.processName})")
        val preferences = XSharedPreferences(BuildConfig.APPLICATION_ID, SHARED_PREFERENCES_NAME)
        val enabled = preferences.getBoolean("AlwaysAllowMultiInstanceSplit", false)
        if (!enabled) return
        
        when (lpparam.packageName) {
            "com.android.systemui" -> {
                try {
                    val method = when {
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.BAKLAVA -> XposedHelpers.findMethodExact(
                            Class.forName(
                                "com.android.wm.shell.common.MultiInstanceHelper", false, lpparam.classLoader
                            ),
                            "supportsMultiInstanceSplit",
                            Int::class.java, // wrong order in compiled code
                            ComponentName::class.java,
                        )
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> XposedHelpers.findMethodExact(
                            Class.forName(
                                "com.android.wm.shell.common.MultiInstanceHelper", false, lpparam.classLoader
                            ),
                            "supportsMultiInstanceSplit",
                            ComponentName::class.java,
                        )
                        else -> XposedHelpers.findMethodExact(
                            Class.forName(
                                "com.android.wm.shell.splitscreen.SplitScreenController", false, lpparam.classLoader
                            ),
                            "supportMultiInstancesSplit",
                            ComponentName::class.java,
                        )
                    }
                    XposedBridge.hookMethod(method, XC_MethodReplacement.returnConstant(true))
                    log("Hooked ${method.declaringClass.name}::${method.name}")
                } catch (e: Throwable) {
                    log("Failed to hook supportsMultiInstanceSplit", e)
                }
            }
            "android" -> {
                try {
                    val ActivityStarterClass = Class.forName("com.android.server.wm.ActivityStarter", false, lpparam.classLoader)
                    val ActivityStarterRequestClass = Class.forName(ActivityStarterClass.name + "\$Request", false, lpparam.classLoader)
                    XposedHelpers.findAndHookMethod(
                        ActivityStarterClass,
                        "executeRequest",
                        ActivityStarterRequestClass,
                        object : XC_MethodHook() {
                            override fun beforeHookedMethod(param: MethodHookParam) {
                                val request = param.args[0]
                                val aInfo = XposedHelpers.getObjectField(request, "activityInfo") as ActivityInfo
                                aInfo.launchMode = ActivityInfo.LAUNCH_MULTIPLE
                            }
                        },
                    )
                    log("Hooked com.android.server.wm.ActivityStarter::executeRequest")
                } catch (e: Throwable) {
                    log("Failed to hook com.android.server.wm.ActivityStarter::executeRequest", e)
                }
            }
        }
    }
}
