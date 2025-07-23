package de.binarynoise.AlwaysAllowChargingSounds

import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

class Hook : IXposedHookLoadPackage {

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        val NotifierClass = XposedHelpers.findClass("com.android.server.power.Notifier", lpparam.classLoader)
        XposedHelpers.findAndHookMethod(NotifierClass, "isChargingFeedbackEnabled", Int::class.java, XC_MethodReplacement.returnConstant(true))
    }

}
