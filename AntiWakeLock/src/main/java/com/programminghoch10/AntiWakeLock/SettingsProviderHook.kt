package com.programminghoch10.AntiWakeLock

import android.os.Bundle
import android.provider.Settings
import android.util.Log
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

class SettingsProviderHook : IXposedHookLoadPackage {
    val TAG = SettingsProviderHook::class.simpleName
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName != "android") return
        val settingsProviderClass = XposedHelpers.findClass("com.android.providers.settings.SettingsProvider", lpparam.classLoader)
        XposedHelpers.findAndHookMethod(
            settingsProviderClass,
            "call",
            String::class.java,
            String::class.java,
            String::class.java,
            object : XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam) {
                    val method = param.args[0] as String
                    val name = param.args[1] as String
                    val args = param.args[2] as Bundle
                    val value = args.getString(Settings.NameValueTable.VALUE)
                    Log.d(TAG, "call: method=$method name=$name value=$value args=$args")
                    if (method != "PUT_global") // Settings.CALL_METHOD_PUT_GLOBAL 
                        return
                    if (name == Settings.Global.STAY_ON_WHILE_PLUGGED_IN) param.result = null
                }
            },
        )
        XposedBridge.hookAllMethods(settingsProviderClass, "mutateGlobalSetting", object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                val name = param.args[0] as String
                val value = param.args[1] as String
                Log.d(TAG, "mutateGlobalSetting: name=$name value=$value")
                if (name == Settings.Global.STAY_ON_WHILE_PLUGGED_IN) param.result = false
            }
        })
        Log.d(TAG, "handleLoadPackage: hooked ${settingsProviderClass.simpleName}")
    }
}
