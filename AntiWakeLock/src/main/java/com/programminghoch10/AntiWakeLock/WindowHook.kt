package com.programminghoch10.AntiWakeLock

import android.os.Build
import android.view.SurfaceView
import android.view.View
import android.view.Window
import android.view.WindowManager
import de.binarynoise.logger.Logger.log
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement.DO_NOTHING
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

class WindowHook : IXposedHookLoadPackage {
    fun filterKeepScreenOnFlag(flags: Int): Int {
        return flags and WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON.inv()
    }
    
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        XposedHelpers.findAndHookMethod(
            Window::class.java,
            "addFlags",
            Int::class.java,
            object : XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam) {
                    log("removing FLAG_KEEP_SCREEN_ON from addFlags")
                    param.args[0] = filterKeepScreenOnFlag(param.args[0] as Int)
                }
            },
        )
        XposedHelpers.findAndHookMethod(
            Window::class.java,
            "setFlags", Int::class.java, Int::class.java,
            object : XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam) {
                    log("removing FLAG_KEEP_SCREEN_ON from setFlags")
                    param.args[0] = filterKeepScreenOnFlag(param.args[0] as Int)
                    param.args[1] = filterKeepScreenOnFlag(param.args[1] as Int)
                }
            },
        )
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            try {
                val AttachInfoClass = Class.forName(View::class.java.name + "\$AttachInfo", false, lpparam.classLoader)
                XposedHelpers.findAndHookMethod(
                    SurfaceView::class.java,
                    "performCollectViewAttributes",
                    AttachInfoClass,
                    Int::class.java,
                    object : XC_MethodHook() {
                        override fun afterHookedMethod(param: MethodHookParam) {
                            val attachInfo = param.args[0]
                            XposedHelpers.setBooleanField(attachInfo, "mKeepScreenOn", false)
                        }
                    },
                )
            } catch (e: Throwable) {
                log("failed to hook SurfaceView.performCollectViewAttributes", e)
            }
        }
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            try {
                val ViewRootImplClass = Class.forName("android.view.ViewRootImpl", false, lpparam.classLoader)
                XposedHelpers.findAndHookMethod(
                    ViewRootImplClass,
                    "applyKeepScreenOnFlag",
                    WindowManager.LayoutParams::class.java,
                    DO_NOTHING,
                )
                log("hooked applyKeepScreenOnFlag to do nothing")
            } catch (e: Throwable) {
                log("failed to hook ViewRootImpl.applyKeepScreenOnFlag", e)
            }
        }
    }
}
