package de.binarynoise.AlwaysAllowChargingFeedback

import java.util.concurrent.atomic.AtomicBoolean
import android.content.Context
import android.provider.Settings
import android.util.Log
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

class Hook : IXposedHookLoadPackage {
    
    private val TAG = "Logger"
    
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        XposedBridge.log("attempting to hook ${lpparam.packageName} @ ${lpparam.processName}")
        if (lpparam.packageName != "android") return
        
        val NotifierClass = XposedHelpers.findClass("com.android.server.power.Notifier", lpparam.classLoader)
        
        XposedHelpers.findAndHookMethod(NotifierClass, "isChargingFeedbackEnabled", Int::class.java, object : XC_MethodReplacement() {
            override fun replaceHookedMethod(param: MethodHookParam): Boolean {
                Log.d(TAG, "replaceHookedMethod: isChargingFeedbackEnabled called")
                Log.e(TAG, "replaceHookedMethod: "  , Throwable())
                val mIsPlayingChargingStartedFeedback = XposedHelpers.getObjectField(param.thisObject, "mIsPlayingChargingStartedFeedback") as AtomicBoolean
                mIsPlayingChargingStartedFeedback.set(false)
                Log.d(TAG, "replaceHookedMethod: mIsPlayingChargingStartedFeedback = ${mIsPlayingChargingStartedFeedback.get()}")
                
                val userId = param.args[0] as Int
                val context = XposedHelpers.getObjectField(param.thisObject, "mContext") as Context
                // charging_sounds_enabled comes from Settings.Secure.CHARGING_SOUNDS_ENABLED which is @hide
                val charging_sounds_enabled = XposedHelpers.callStaticMethod(
                    Settings.Secure::class.java, "getIntForUser", context.contentResolver, "charging_sounds_enabled", 1, userId
                ) != 0
                Log.d(TAG, "replaceHookedMethod: charging_sounds_enabled = ${charging_sounds_enabled}")
                return charging_sounds_enabled
            }
        })
        
        XposedHelpers.findAndHookMethod(NotifierClass, "playChargingStartedFeedback",
            Int::class.java, Boolean::class.java,
            object: XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                Log.d(TAG, "beforeHookedMethod: playChargingStartedFeedback")
                val mIsPlayingChargingStartedFeedback = XposedHelpers.getObjectField(param.thisObject, "mIsPlayingChargingStartedFeedback") as AtomicBoolean
                Log.d(TAG, "replaceHookedMethod: mIsPlayingChargingStartedFeedback = ${mIsPlayingChargingStartedFeedback.get()}")
                
            }
        })
        
        XposedHelpers.findAndHookMethod(NotifierClass, "showWiredChargingStarted",
            Int::class.java,
            object : XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam) {
                    Log.d(TAG, "beforeHookedMethod: showWiredChargingStarted")
                }
            })
        
        NotifierClass.declaredMethods
            .filter { it.name == "playChargingStartedFeedback" || it.name == "showWiredChargingStarted"}
            .forEach {
                XposedBridge.hookMethod(it, object : XC_MethodHook() {
                    override fun beforeHookedMethod(param: MethodHookParam) {
                        Log.d(TAG, "beforeHookedMethod: ${param.method}")
                    }
                })
                Log.d(TAG, "handleLoadPackage: hooked ${it}")
                XposedBridge.log("handleLoadPackage: hooked ${it}")
            }
        
        Log.d(TAG, "handleLoadPackage: hooked ${lpparam.packageName}")
        XposedBridge.log("hooked charging stuff in ${lpparam.packageName}")
    }
}
