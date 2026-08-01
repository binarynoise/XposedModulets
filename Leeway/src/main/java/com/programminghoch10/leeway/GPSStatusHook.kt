package com.programminghoch10.leeway

import de.binarynoise.logger.Logger.log
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

class GPSStatusHook : IXposedHookLoadPackage {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName != "com.eclipsim.gpsstatus2") return
        val ApplicationPackageManager = XposedHelpers.findClass("android.app.ApplicationPackageManager", lpparam.classLoader)
        XposedHelpers.findAndHookMethod(
            ApplicationPackageManager,
            "getPackageInfo",
            String::class.java,
            Int::class.java,
            object : XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam) {
                    val packageName = param.args[0] as String
                    if (packageName == "com.eclipsim.gpstoolbox.pro") {
                        log("rewrite signature target package")
                        param.args[0] = "com.eclipsim.gpsstatus2"
                    }
                }
            },
        )
        XposedHelpers.findAndHookMethod(
            ApplicationPackageManager,
            "getInstallerPackageName",
            String::class.java,
            XC_MethodReplacement.returnConstant("com.android.vending"),
        )
    }
}
