package de.binarynoise.betterVerboseWiFiLogging

import android.annotation.SuppressLint
import de.binarynoise.betterVerboseWiFiLogging.StandardWifiEntry.newGetScanResultDescription
import de.binarynoise.betterVerboseWiFiLogging.Utils.wifiTrackerLibUtilsClass
import de.binarynoise.betterVerboseWiFiLogging.WifiEntry.getNetworkCapabilityDescription
import de.binarynoise.betterVerboseWiFiLogging.WifiEntry.getNetworkSelectionDescription
import de.binarynoise.betterVerboseWiFiLogging.WifiEntry.getWifiInfoDescription
import de.binarynoise.betterVerboseWiFiLogging.WifiEntry.newGetWifiInfoDescription
import de.binarynoise.betterVerboseWiFiLogging.WifiEntry.wifiEntryClass
import de.binarynoise.betterVerboseWiFiLogging.Wrapper.classLoader
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import de.robv.android.xposed.XC_MethodHook as MethodHook

@SuppressLint("PrivateApi")
class Hook : IXposedHookLoadPackage {
    
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        classLoader = lpparam.classLoader
        
        XposedBridge.hookMethod(getWifiInfoDescription, object : MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                with(param) {
                    result = newGetWifiInfoDescription(thisObject)
                }
            }
        })
        
        run {
            val hook = object : MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam) {
                    with(param) {
                        result = newGetScanResultDescription(thisObject)
                    }
                }
            }
            listOf(WifiEntry.getScanResultDescription, StandardWifiEntry.getScanResultDescription).forEach {
                XposedBridge.hookMethod(it, hook)
            }
        }
        
        
        XposedHelpers.findAndHookMethod(wifiTrackerLibUtilsClass, "getVerboseLoggingDescription", wifiEntryClass, object : MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                with(param) {
                    val wifiEntry = args[0]
                    
                    if (!BaseWifiTracker.isVerboseLoggingEnabled() || wifiEntry == null) {
                        result = ""
                        return
                    }
                    
                    result = listOf(
                        newGetWifiInfoDescription(wifiEntry),
                        getNetworkCapabilityDescription.invoke(wifiEntry) as String?, // can stay as is
                        newGetScanResultDescription(wifiEntry),
                        getNetworkSelectionDescription.invoke(wifiEntry) as String?, // TODO
                    ).filterNot { it.isNullOrBlank() }.joinToString(", ").trim()
                }
            }
        })
    }
}
