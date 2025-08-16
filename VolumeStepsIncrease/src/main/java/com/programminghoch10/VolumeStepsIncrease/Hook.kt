package com.programminghoch10.VolumeStepsIncrease

import android.util.Log
import com.programminghoch10.VolumeStepsIncrease.Common.streams
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XSharedPreferences
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import de.robv.android.xposed.XC_MethodHook as MethodHook

class Hook : IXposedHookLoadPackage {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName == BuildConfig.APPLICATION_ID) return
        
        val SystemPropertiesClass = XposedHelpers.findClass("android.os.SystemProperties", lpparam.classLoader)
        
        XposedHelpers.findAndHookMethod(SystemPropertiesClass, "getInt", String::class.java, Int::class.java, object : MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                val key = param.args[0] as String
                param.args[1] as Int
                val result = param.result as Int
                if (!streams.contains(key)) return
                val preferences = XSharedPreferences(BuildConfig.APPLICATION_ID, "streams")
                if (!preferences.contains(key)) return
                param.result = preferences.getInt(key, result)
                Log.d("Logger", "beforeHookedMethod: replace $key with ${param.result}")
            }
        })
        
        XposedHelpers.findAndHookMethod(SystemPropertiesClass, "getBoolean", String::class.java, Boolean::class.java, object : MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                val key = param.args[0] as String
                if (key == "audio.safemedia.bypass") param.result = true
            }
        })
    }
}
