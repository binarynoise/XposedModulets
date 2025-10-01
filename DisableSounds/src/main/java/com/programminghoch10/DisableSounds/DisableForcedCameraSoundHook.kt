package com.programminghoch10.DisableSounds

import android.content.res.XResources
import android.media.MediaActionSound
import android.os.Build
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.IXposedHookZygoteInit
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

class DisableForcedCameraSoundHook : IXposedHookLoadPackage, IXposedHookZygoteInit {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName == "android") {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val AudioServiceClass = XposedHelpers.findClass("com.android.server.audio.AudioService", lpparam.classLoader)
                XposedHelpers.findAndHookMethod(AudioServiceClass, "isCameraSoundForced", XC_MethodReplacement.returnConstant(false))
                XposedHelpers.findAndHookMethod(AudioServiceClass, "readCameraSoundForced", XC_MethodReplacement.returnConstant(false))
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            XposedHelpers.findAndHookMethod(MediaActionSound::class.java, "mustPlayShutterSound", XC_MethodReplacement.returnConstant(false))
        }
    }
    
    override fun initZygote(startupParam: IXposedHookZygoteInit.StartupParam) {
        XResources.setSystemWideReplacement("android", "bool", "config_camera_sound_forced", false)
    }
}
