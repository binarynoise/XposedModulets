package de.binarynoise.AlwaysAllowChargingSounds

import android.content.Context
import android.provider.Settings
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

class Hook : IXposedHookLoadPackage {
    
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName != "android") return
        
        val NotifierClass = XposedHelpers.findClass("com.android.server.power.Notifier", lpparam.classLoader)
        XposedHelpers.findAndHookMethod(NotifierClass, "isChargingFeedbackEnabled", Int::class.java, object : XC_MethodReplacement() {
            override fun replaceHookedMethod(param: MethodHookParam): Boolean {
                val userId = param.args[0] as Int
                val context = XposedHelpers.getObjectField(param.thisObject, "mContext") as Context
                return XposedHelpers.callStaticMethod(
                    Settings.Secure::class.java, "getIntForUser", context.contentResolver, "charging_sounds_enabled", 1, userId
                ) != 0
            }
        })
    }
}
