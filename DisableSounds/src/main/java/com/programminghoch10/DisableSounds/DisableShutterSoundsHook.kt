package com.programminghoch10.DisableSounds

import android.media.MediaActionSound
import android.os.Build
import com.programminghoch10.DisableSounds.BuildConfig.SHARED_PREFERENCES_NAME
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XSharedPreferences
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

class DisableShutterSoundsHook : IXposedHookLoadPackage {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam?) {
        val sharedPreferences = XSharedPreferences(BuildConfig.APPLICATION_ID, SHARED_PREFERENCES_NAME)
        if (!sharedPreferences.getBoolean("shutter", false)) return
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            for (methodName in listOf("load", "play")) {
                XposedHelpers.findAndHookMethod(
                    MediaActionSound::class.java,
                    methodName,
                    Int::class.java,
                    XC_MethodReplacement.DO_NOTHING,
                )
            }
        }
        
        // TODO: need a native hook for old Camera API methods
        //  https://cs.android.com/android/platform/superproject/main/+/main:frameworks/av/services/camera/libcameraservice/CameraService.cpp;l=4097?q=camera_click.ogg
        //  then the "might not work" warning can be removed from @string/disable_shutter_sounds_description_on
    }
}
