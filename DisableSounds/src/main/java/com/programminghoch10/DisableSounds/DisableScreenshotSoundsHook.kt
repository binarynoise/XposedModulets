package com.programminghoch10.DisableSounds

import android.content.Context
import android.media.MediaActionSound
import android.os.Build
import com.google.common.util.concurrent.Futures
import com.programminghoch10.DisableSounds.BuildConfig.SHARED_PREFERENCES_NAME
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XSharedPreferences
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

class DisableScreenshotSoundsHook : IXposedHookLoadPackage {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName != "com.android.systemui") return
        val sharedPreferences = XSharedPreferences(BuildConfig.APPLICATION_ID, SHARED_PREFERENCES_NAME)
        if (!sharedPreferences.getBoolean("screenshot", false)) return
        
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> XposedHelpers.findAndHookMethod(
                "com.android.systemui.screenshot.ScreenshotSoundControllerImpl",
                lpparam.classLoader,
                "playScreenshotSoundAsync",
                XC_MethodReplacement.DO_NOTHING,
            )
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ->
                // TODO: check if inlined by r8 on 33
                XposedHelpers.findAndHookMethod(
                    "com.android.systemui.screenshot.ScreenshotController",
                    lpparam.classLoader,
                    "playCameraSound",
                    XC_MethodReplacement.DO_NOTHING,
                )
            
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && Build.VERSION.SDK_INT <= Build.VERSION_CODES.TIRAMISU) {
            val ScreenshotControllerClass = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                XposedHelpers.findClass("com.android.systemui.screenshot.ScreenshotController", lpparam.classLoader)
            } else {
                XposedHelpers.findClass("com.android.systemui.screenshot.GlobalScreenshot", lpparam.classLoader)
            }
            
            var replacementDummy: Any = MediaActionSoundDummy()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) replacementDummy = Futures.immediateFuture(replacementDummy)
            
            XposedHelpers.findAndHookConstructor(
                ScreenshotControllerClass, Context::class.java, object : XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam) {
                        XposedHelpers.setObjectField(
                            param.thisObject, "mCameraSound", replacementDummy
                        )
                    }
                })
        }
    }
    
    class MediaActionSoundDummy : MediaActionSound() {
        override fun load(soundName: Int) {}
        override fun play(soundName: Int) {}
        override fun release() {}
    }
}
