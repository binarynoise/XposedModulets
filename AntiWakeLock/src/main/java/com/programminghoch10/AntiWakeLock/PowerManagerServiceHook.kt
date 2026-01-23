package com.programminghoch10.AntiWakeLock

import android.os.Build
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

class PowerManagerServiceHook : IXposedHookLoadPackage {
    fun disableMethodByName(clazz: Class<*>, name: String) {
        val method = clazz.declaredMethods.single { it.name == "name" }
        XposedBridge.hookMethod(method, XC_MethodReplacement.DO_NOTHING)
    }
    
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName != "android") return
        
        val PowerManagerServiceClass = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) XposedHelpers.findClass("com.android.server.power.PowerManagerService", lpparam.classLoader)
        else XposedHelpers.findClass("com.android.server.PowerManagerService", lpparam.classLoader)
        
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            disableMethodByName(PowerManagerServiceClass, "acquireWakeLock")
            disableMethodByName(PowerManagerServiceClass, "releaseWakeLock")
        }
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            disableMethodByName(PowerManagerServiceClass, "acquireWakeLockInternal")
            disableMethodByName(PowerManagerServiceClass, "releaseWakeLockInternal")
        }
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val BinderServiceClass = XposedHelpers.findClass("${PowerManagerServiceClass.name}\$BinderService", lpparam.classLoader)
            disableMethodByName(BinderServiceClass, "acquireWakeLock")
            disableMethodByName(BinderServiceClass, "releaseWakeLock")
        }
    }
}
