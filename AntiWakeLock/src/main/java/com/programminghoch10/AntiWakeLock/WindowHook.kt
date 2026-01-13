package com.programminghoch10.AntiWakeLock

import android.util.Log
import android.view.Window
import android.view.WindowManager
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

class WindowHook : IXposedHookLoadPackage {
    fun filterKeepScreenOnFlag(flags: Int) : Int {
        return flags and WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON.inv()
    }

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        XposedHelpers.findAndHookMethod(Window::class.java, "addFlags", Int::class.java, object: XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                param.args[0] = filterKeepScreenOnFlag(param.args[0] as Int)
            }
        })
        XposedHelpers.findAndHookMethod(Window::class.java, "setFlags", Int::class.java, Int::class.java, object: XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                param.args[0] = filterKeepScreenOnFlag(param.args[0] as Int)
                param.args[1] = filterKeepScreenOnFlag(param.args[1] as Int)
            }
        })
    }
}
