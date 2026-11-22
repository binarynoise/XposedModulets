package com.programminghoch10.AntiWakeLock

import android.os.Build
import android.provider.Settings
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.callbacks.XC_LoadPackage

class SettingsHook : IXposedHookLoadPackage {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) return
        Settings.Global::class.java.methods.filter { it.name.startsWith("put") }.forEach {
                XposedBridge.hookMethod(it, object : XC_MethodHook() {
                    override fun beforeHookedMethod(param: MethodHookParam) {
                        val name = param.args[1] as String
                        if (name == Settings.Global.STAY_ON_WHILE_PLUGGED_IN) param.result = false
                    }
                })
            }
    }
}
