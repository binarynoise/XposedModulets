package de.binarynoise.AlwaysAllowMultiInstanceSplit

import android.content.ComponentName
import android.content.pm.ActivityInfo
import android.os.Build
import de.binarynoise.logger.Logger.log
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import de.robv.android.xposed.XC_MethodHook as MethodHook

class Hook : IXposedHookLoadPackage {
    
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        log("handleLoadPackage(${lpparam.packageName} in process ${lpparam.processName})")
        
        when (lpparam.packageName) {
            "com.android.systemui" -> {
                try {
                    val method = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                        XposedHelpers.findMethodExact(
                            Class.forName("com.android.wm.shell.common.MultiInstanceHelper", false, lpparam.classLoader),
                            "supportsMultiInstanceSplit",
                            ComponentName::class.java,
                        )
                    } else {
                        XposedHelpers.findMethodExact(
                            Class.forName("com.android.wm.shell.splitscreen.SplitScreenController", false, lpparam.classLoader),
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
                        object : MethodHook() {
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
