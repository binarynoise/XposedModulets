package de.binarynoise.muteSlf4jWarnings

import android.annotation.SuppressLint
import de.binarynoise.reflection.method
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.callbacks.XC_LoadPackage
import de.robv.android.xposed.XC_MethodHook as MethodHook

@SuppressLint("PrivateApi", "MissingPermission")
class Hook : IXposedHookLoadPackage {
    
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        val errorPrintStreamClass = lpparam.classLoader.loadClass("com.android.internal.os.AndroidPrintStream")
        
        val println by method(errorPrintStreamClass, String::class.java)
        val print by method(errorPrintStreamClass, String::class.java)
        
        val hook = object : MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                val msg = param.args[0] as String
                if (msg.startsWith("SLF4J:")) {
                    param.setResult(Unit)
                }
            }
        }
        XposedBridge.hookMethod(println, hook)
        XposedBridge.hookMethod(print, hook)
    }
}
