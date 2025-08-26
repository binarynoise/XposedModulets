package com.programminghoch10.PreventAudioFocus

import android.media.AudioManager
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XSharedPreferences
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam

class Hook : IXposedHookLoadPackage {
    override fun handleLoadPackage(lpparam: LoadPackageParam) {
        var clazz: Class<*>? = AudioManager::class.java
        if (lpparam.packageName == "android") clazz = XposedHelpers.findClass(
            "com.android.server.audio.MediaFocusControl", lpparam.classLoader
        )
        val sharedPreferences = XSharedPreferences(BuildConfig.APPLICATION_ID, SHARED_PREFERENCES_NAME)
        val constant = ENTRIES.map {
            Triple(
                it.key,
                it.value,
                sharedPreferences.getBoolean(it.key, false),
            )
        }.find { it.third }?.second ?: ENTRIES_DEFAULT
        XposedBridge.hookAllMethods(
            clazz, "requestAudioFocus", XC_MethodReplacement.returnConstant(constant)
        )
    }
}

