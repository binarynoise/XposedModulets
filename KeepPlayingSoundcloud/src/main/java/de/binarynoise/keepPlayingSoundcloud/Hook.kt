package de.binarynoise.keepPlayingSoundcloud

import android.content.Intent
import de.binarynoise.logger.Logger.log
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import de.robv.android.xposed.XC_MethodHook as MethodHook


class Hook : IXposedHookLoadPackage {
    
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName != "com.soundcloud.android") return
        
        val MediaServiceClass = lpparam.classLoader.loadClass("com.soundcloud.android.playback.players.MediaService")
        
        XposedHelpers.findAndHookMethod(MediaServiceClass, "onTaskRemoved", Intent::class.java, object : MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                with(param) {
                    result = null // Prevent the original method from running
                }
            }
        })
        
        log("hooked onTaskRemoved method")
    }
}
