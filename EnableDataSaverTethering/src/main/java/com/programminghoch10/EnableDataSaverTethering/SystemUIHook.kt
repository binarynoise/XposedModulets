package com.programminghoch10.EnableDataSaverTethering

import de.binarynoise.reflection.isCurrentThreadCalledFromClass
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

class SystemUIHook : IXposedHookLoadPackage {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName != "com.android.systemui") return
        
        val NetworkPolicyManagerClass = XposedHelpers.findClass("android.net.NetworkPolicyManager", lpparam.classLoader)
        val HotspotTileClass = XposedHelpers.findClass("com.android.systemui.qs.tiles.HotspotTile", lpparam.classLoader)
        XposedHelpers.findAndHookMethod(NetworkPolicyManagerClass, "getRestrictBackground", object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                if (isCurrentThreadCalledFromClass(HotspotTileClass)) param.result = false
            }
        })
        
        val CallbackInfoClass = XposedHelpers.findClass(HotspotTileClass.name + $$"$CallbackInfo", lpparam.classLoader)
        XposedBridge.hookMethod(HotspotTileClass.methods.single { it.name == "handleUpdateState" }, object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                val obj = param.args[1] ?: return
                if (obj::class.java == CallbackInfoClass) {
                    XposedHelpers.setBooleanField(obj, "isDataSaverEnabled", false)
                }
            }
        })
    }
}
