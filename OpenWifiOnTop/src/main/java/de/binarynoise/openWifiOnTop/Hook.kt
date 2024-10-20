package de.binarynoise.openWifiOnTop

import de.binarynoise.reflection.cast
import de.binarynoise.reflection.findDeclaredField
import de.binarynoise.reflection.findDeclaredMethod
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import de.robv.android.xposed.XC_MethodHook as MethodHook

class Hook : IXposedHookLoadPackage {
    
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        val WifiEntryClass = lpparam.classLoader.loadClass("com.android.wifitrackerlib.WifiEntry")
        val getSecurityTypes = WifiEntryClass.findDeclaredMethod("getSecurityTypes")
        
        val WifiPickerTrackerClass = lpparam.classLoader.loadClass("com.android.wifitrackerlib.WifiPickerTracker")
        
        try {
            XposedHelpers.findAndHookMethod(WifiPickerTrackerClass, "getWifiEntries", object : MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam) = with(param) {
                    result = result.cast<List<*>>().sortedByDescending {
                        getSecurityTypes.invoke(it)!!.cast<List<Int>>().minOrNull() == 0
                    }
                }
            })
        } catch (_: Throwable) {
        }
        
        try {
            XposedBridge.hookAllMethods(WifiPickerTrackerClass, "updateWifiEntries", object : MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam): Unit = with(param) {
                    val mWifiEntries = WifiPickerTrackerClass.findDeclaredField("mWifiEntries")
                    mWifiEntries.set(param.thisObject, mWifiEntries.get(thisObject)!!.cast<List<*>>().sortedByDescending {
                        getSecurityTypes.invoke(it)?.cast<List<Int>>()?.minOrNull() == 0
                    }.let(::ArrayList)) // convert java.util.Arrays$ArrayList to java.util.ArrayList
                }
            })
        } catch (_: Throwable) {
        }
    }
}
