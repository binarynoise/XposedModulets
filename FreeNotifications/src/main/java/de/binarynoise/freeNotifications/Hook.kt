package de.binarynoise.freeNotifications

import android.app.NotificationChannel
import de.binarynoise.reflection.findDeclaredField
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import de.robv.android.xposed.XC_MethodHook as MethodHook

class Hook : IXposedHookLoadPackage {
    
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        val cls = NotificationChannel::class.java
        
        val mBlockableSystem = cls.findDeclaredField("mBlockableSystem")
        
        val setToTrue = object : MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                mBlockableSystem.set(param.thisObject, true)
            }
            
            override fun afterHookedMethod(param: MethodHookParam) {
                mBlockableSystem.set(param.thisObject, true)
            }
        }
        
        try {
            XposedBridge.hookAllConstructors(cls, setToTrue)
        } catch (_: Throwable) {
        }
        try {
            XposedHelpers.findAndHookMethod(cls, "setBlockable", Boolean::class.java, setToTrue)
        } catch (_: Throwable) {
        }
        try {
            XposedHelpers.findAndHookMethod(cls, "isBlockable", setToTrue)
        } catch (_: Throwable) {
        }
    }
}
