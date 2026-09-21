package com.programminghoch10.EnableDataSaverTethering

import android.content.Context
import android.widget.LinearLayout
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

class SettingsHook : IXposedHookLoadPackage {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName != "com.android.settings") return
        
        val TetherSettingsClass = XposedHelpers.findClass("com.android.settings.network.tether.TetherSettings", lpparam.classLoader)
        XposedBridge.hookAllMethods(TetherSettingsClass, "onCreate", object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                XposedHelpers.setBooleanField(param.thisObject, "mDataSaverEnabled", false)
            }
        })
        XposedHelpers.findAndHookMethod(
            TetherSettingsClass,
            "onDataSaverChanged",
            Boolean::class.java,
            XC_MethodReplacement.DO_NOTHING,
        )
        
        // all the following is for Wi-Fi tethering only 👀
        
        val WifiTetherPreferenceControllerClass =
            XposedHelpers.findClass("com.android.settings.wifi.tether.WifiTetherPreferenceController", lpparam.classLoader)
        XposedHelpers.findAndHookMethod(
            WifiTetherPreferenceControllerClass,
            "setDataSaverEnabled",
            Boolean::class.java,
            XC_MethodReplacement.DO_NOTHING,
        )
        XposedBridge.hookAllConstructors(WifiTetherPreferenceControllerClass, object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                XposedHelpers.setBooleanField(param.thisObject, "mDataSaverEnabled", false)
            }
        })
        XposedHelpers.findAndHookMethod(WifiTetherPreferenceControllerClass, "canEnabled", object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                param.result = true
            }
        })
        
        val WifiTetherSwitchBarControllerClass =
            XposedHelpers.findClass("com.android.settings.wifi.tether.WifiTetherSwitchBarController", lpparam.classLoader)
        XposedHelpers.findAndHookMethod(
            WifiTetherSwitchBarControllerClass,
            "onDataSaverChanged",
            Boolean::class.java,
            XC_MethodReplacement.DO_NOTHING,
        )
        XposedHelpers.findAndHookMethod(WifiTetherSwitchBarControllerClass, "updateWifiSwitch", object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                val mSwitchBar = XposedHelpers.getObjectField(param.thisObject, "mSwitchBar") as LinearLayout
                mSwitchBar.isEnabled = true
            }
        })
        
        val WifiHotspotScreenClass = XposedHelpers.findClass("com.android.settings.wifi.tether.WifiHotspotScreen", lpparam.classLoader)
        // we are ignoring super.isEnabled here because I can't find a good way to implement that
        XposedHelpers.findAndHookMethod(
            WifiHotspotScreenClass,
            "isEnabled",
            Context::class.java,
            XC_MethodReplacement.returnConstant(true),
        )
    }
}
