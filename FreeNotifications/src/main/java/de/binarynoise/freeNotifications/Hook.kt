package de.binarynoise.freeNotifications

import android.app.NotificationChannel
import de.binarynoise.logger.Logger.log
import de.binarynoise.reflection.findDeclaredField
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodReplacement.DO_NOTHING
import de.robv.android.xposed.XC_MethodReplacement.returnConstant
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import de.robv.android.xposed.XC_MethodHook as MethodHook

class Hook : IXposedHookLoadPackage {
    
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        // AOSP: mBlockableSystem / setBlockable / isBlockable
        
        val cls = NotificationChannel::class.java
        
        val mBlockableSystem = cls.findDeclaredField("mBlockableSystem")
        
        tryAndLog("hook NotificationChannel constructors") {
            XposedBridge.hookAllConstructors(cls, object : MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam) {
                    mBlockableSystem.set(param.thisObject, true)
                }
            })
        }
        tryAndLog("hook setBlockable") {
            XposedHelpers.findAndHookMethod(cls, "setBlockable", Boolean::class.java, DO_NOTHING)
        }
        tryAndLog("hook isBlockable") {
            XposedHelpers.findAndHookMethod(cls, "isBlockable", returnConstant(true))
        }
        
        // AOSP: mImportanceLockedDefaultApp / setImportanceLockedByCriticalDeviceFunction / isImportanceLockedByCriticalDeviceFunction
        
        val mImportanceLockedDefaultApp = cls.findDeclaredField("mImportanceLockedDefaultApp")
        
        tryAndLog("hook constructors") {
            XposedBridge.hookAllConstructors(cls, object : MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam) {
                    mImportanceLockedDefaultApp.set(param.thisObject, false)
                }
            })
        }
        tryAndLog("hook setImportanceLockedByCriticalDeviceFunction") {
            XposedHelpers.findAndHookMethod(cls, "setImportanceLockedByCriticalDeviceFunction", Boolean::class.java, DO_NOTHING)
        }
        tryAndLog("hook isImportanceLockedByCriticalDeviceFunction") {
            XposedHelpers.findAndHookMethod(cls, "isImportanceLockedByCriticalDeviceFunction", returnConstant(false))
        }
    }
}

private inline fun tryAndLog(message: String, block: () -> Unit) {
    log(message)
    return try {
        block()
        log("done!")
    } catch (t: Throwable) {
        log("failed!")
        XposedBridge.log(t)
    }
}
