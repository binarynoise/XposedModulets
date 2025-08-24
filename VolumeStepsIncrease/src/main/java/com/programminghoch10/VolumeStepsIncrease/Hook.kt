package com.programminghoch10.VolumeStepsIncrease

import android.util.Log
import com.programminghoch10.VolumeStepsIncrease.Common.SHARED_PREFERENCES_NAME
import com.programminghoch10.VolumeStepsIncrease.Common.STREAMS
import com.programminghoch10.VolumeStepsIncrease.Common.getPreferenceKey
import com.programminghoch10.VolumeStepsIncrease.Common.systemPropertyToStream
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XSharedPreferences
import de.robv.android.xposed.XposedBridge
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
                val streamInt = systemPropertyToStream[key] ?: return
                val preferences = XSharedPreferences(BuildConfig.APPLICATION_ID, SHARED_PREFERENCES_NAME)
                if (!preferences.contains(getPreferenceKey(streamInt))) return
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
        
        val AudioServiceClass = XposedHelpers.findClass("com.android.server.audio.AudioService", lpparam.classLoader)
        XposedBridge.hookAllConstructors(AudioServiceClass, object : MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                val MAX_STREAM_VOLUME = XposedHelpers.getObjectField(param.thisObject, "MAX_STREAM_VOLUME") as Array<Int>
                val preferences = XSharedPreferences(BuildConfig.APPLICATION_ID, SHARED_PREFERENCES_NAME)
                STREAMS.filter { preferences.contains(getPreferenceKey(it.value)) }
                    .map { it.value to preferences.getInt(getPreferenceKey(it.value), MAX_STREAM_VOLUME[it.value]) }
                    .forEach { MAX_STREAM_VOLUME[it.first] = it.second }
            }
        })
    }
}
