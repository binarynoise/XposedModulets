package com.programminghoch10.RotationControl

import android.app.Activity
import android.content.ContextWrapper
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.view.Window
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XSharedPreferences
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam

class XposedHook : IXposedHookLoadPackage {
    override fun handleLoadPackage(lpparam: LoadPackageParam) {
        if (lpparam.packageName == BuildConfig.APPLICATION_ID) return
        
        XposedHelpers.findAndHookMethod(
            Activity::class.java,
            "setRequestedOrientation",
            Int::class.java,
            object : XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam) {
                    val originalRotationMode = ROTATION_MODE.entries.find { it.value == param.args[0] } ?: ROTATION_MODE.SCREEN_ORIENTATION_UNSET
                    val sharedPreferences: SharedPreferences = XSharedPreferences(BuildConfig.APPLICATION_ID, SHARED_PREFERENCES_NAME)
                    var selectedRotationMode = ROTATION_MODE.entries.find { sharedPreferences.getBoolean(it.key, false) } ?: ROTATION_MODE_DEFAULT
                    if (selectedRotationMode == ROTATION_MODE.SCREEN_ORIENTATION_UNSET) selectedRotationMode = originalRotationMode
                    if (sharedPreferences.getBoolean("rewrite_locked_orientations", false)) {
                        selectedRotationMode = rewriteLockedOrientation.get(selectedRotationMode) ?: selectedRotationMode
                    }
                    if (sharedPreferences.getBoolean("rewrite_sensor_orientations", false)) {
                        selectedRotationMode = rewriteSensorOrientation.get(selectedRotationMode) ?: selectedRotationMode
                    }
                    if (selectedRotationMode == ROTATION_MODE.SCREEN_ORIENTATION_UNSET) return
                    param.args[0] = selectedRotationMode.value
                }
            },
        )
        
        XposedHelpers.findAndHookMethod(
            "com.android.internal.policy.PhoneWindow",
            lpparam.classLoader,
            "generateLayout",
            "com.android.internal.policy.DecorView",
            object : XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam) {
                    var context = (param.thisObject as Window).context
                    while (context is ContextWrapper) {
                        if (context is Activity) {
                            // we only need to call setRequestedOrientation
                            // the value doesn't matter, since the hook above replaces it
                            context.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                            return
                        }
                        context = context.baseContext
                    }
                }
            },
        )
    }
}
