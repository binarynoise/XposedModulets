package com.programminghoch10.DisableSounds

import android.content.ContentResolver
import android.provider.Settings
import com.programminghoch10.DisableSounds.BuildConfig.SHARED_PREFERENCES_NAME
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XSharedPreferences
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

class DisableChargingSoundsHook : IXposedHookLoadPackage {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName != "android") return
        val sharedPreferences = XSharedPreferences(BuildConfig.APPLICATION_ID, SHARED_PREFERENCES_NAME)
        if (!sharedPreferences.getBoolean("charging", false)) return
        val disableChargingFeedback = sharedPreferences.getBoolean("chargingFeedback", false)
        
        val CHARGING_STARTED_SOUND = XposedHelpers.getStaticObjectField(Settings::class.java, "CHARGING_STARTED_SOUND") as String
        val WIRELESS_CHARGING_STARTED_SOUND = XposedHelpers.getStaticObjectField(Settings::class.java, "WIRELESS_CHARGING_STARTED_SOUND") as String
        val CHARGING_VIBRATION_ENABLED = XposedHelpers.getStaticObjectField(Settings.Secure::class.java, "CHARGING_VIBRATION_ENABLED") as String
        
        XposedHelpers.findAndHookMethod(
            Settings.Global::class.java,
            "getString",
            ContentResolver::class.java,
            String::class.java,
            object : XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam) {
                    val string = param.args[1] as String
                    if (string == CHARGING_STARTED_SOUND || string == WIRELESS_CHARGING_STARTED_SOUND) param.result = null
                }
            },
        )
        
        if (disableChargingFeedback) {
            XposedHelpers.findAndHookMethod(
                Settings.Secure::class.java,
                "getIntForUser",
                ContentResolver::class.java,
                String::class.java,
                Int::class.java,
                object : XC_MethodHook() {
                    override fun beforeHookedMethod(param: MethodHookParam) {
                        val string = param.args[1] as String
                        if (string == CHARGING_VIBRATION_ENABLED) param.result = 0
                    }
                },
            )
            
            val NotifierClass = XposedHelpers.findClass("com.android.server.power.Notifier", lpparam.classLoader)
            XposedHelpers.findAndHookMethod(
                NotifierClass,
                "isChargingFeedbackEnabled",
                Int::class.java,
                XC_MethodReplacement.returnConstant(false),
            )
        }
    }
}
