package com.programminghoch10.GrantAllPermissions

import android.content.pm.PackageManager
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import de.robv.android.xposed.XC_MethodHook as MethodHook

class Hook : IXposedHookLoadPackage {
    
    private val returnPermissionGrantedHook = XC_MethodReplacement.returnConstant(PackageManager.PERMISSION_GRANTED)
    
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        when (lpparam.packageName) {
            "android" -> androidHook(lpparam)
            else -> appHook(lpparam)
        }
    }
    
    private fun androidHook(lpparam: XC_LoadPackage.LoadPackageParam) {
        val ActivityManagerServiceClass = XposedHelpers.findClass("com.android.server.am.ActivityManagerService", lpparam.classLoader)
        XposedBridge.hookAllMethods(ActivityManagerServiceClass, "checkComponentPermission", returnPermissionGrantedHook)
        XposedBridge.hookAllMethods(ActivityManagerServiceClass, "checkPermissionForDevice", returnPermissionGrantedHook)
        XposedBridge.hookAllMethods(ActivityManagerServiceClass, "checkPermission", returnPermissionGrantedHook)
        val PermissionManagerServiceClass = XposedHelpers.findClass("com.android.server.pm.permission.PermissionManagerService", lpparam.classLoader)
        XposedBridge.hookAllMethods(PermissionManagerServiceClass, "checkPermission", returnPermissionGrantedHook)
        XposedBridge.hookAllMethods(PermissionManagerServiceClass, "checkUidPermission", returnPermissionGrantedHook)
        XposedHelpers.findAndHookMethod(
            PermissionManagerServiceClass,
            "getAllPermissionStates",
            String::class.java,
            String::class.java,
            Int::class.java,
            object : MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam) {
                    @Suppress("UNCHECKED_CAST") val result = param.result as Map<String, Object>
                    result.forEach { (_, value) -> XposedHelpers.setBooleanField(value, "mGranted", true) }
                }
            })
    }
    
    private fun appHook(lpparam: XC_LoadPackage.LoadPackageParam) {
        val PermissionManagerClass = XposedHelpers.findClass("android.permission.PermissionManager", lpparam.classLoader)
        XposedHelpers.findAndHookMethod(PermissionManagerClass, "checkPermission", returnPermissionGrantedHook)
        XposedHelpers.findAndHookMethod(PermissionManagerClass, "checkPermissionUncached", returnPermissionGrantedHook)
        val PermissionCheckerManagerClass = XposedHelpers.findClass("android.permission.PermissionChecker", lpparam.classLoader)
        XposedBridge.hookAllMethods(PermissionCheckerManagerClass, "checkPermission", returnPermissionGrantedHook)
    }
}
