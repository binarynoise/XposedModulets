package de.binarynoise.resetallnotificationchannels

import android.annotation.SuppressLint
import android.util.Log
import androidx.annotation.Keep
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.callbacks.XC_LoadPackage

const val TAG = "ResetAllNotifications"

@Keep
@SuppressLint("PrivateApi")
class Hook : IXposedHookLoadPackage {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName != "android") return
        
        val notificationManagerServiceClass = Class.forName("com.android.server.notification.NotificationManagerService", false, lpparam.classLoader)
        
        XposedBridge.hookMethod(notificationManagerServiceClass.declaredMethods.find { it.name == "loadPolicyFile" }, object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                val thisObject = param.thisObject
                
                val mPreferenceHelperField = notificationManagerServiceClass.getDeclaredField("mPreferencesHelper")
                val preferencesHelperClass = Class.forName("com.android.server.notification.PreferencesHelper", false, lpparam.classLoader)
                val mPackagePreferencesField = preferencesHelperClass.getDeclaredField("mPackagePreferences")
                
                val mPreferencesHelper = mPreferenceHelperField.apply { isAccessible = true }.get(thisObject)!!
                val mPackagePreferences = mPackagePreferencesField.apply { isAccessible = true }.get(mPreferencesHelper) as MutableMap<*, *>
                
                println("mPackagePreferences has ${mPackagePreferences.size} entries")
                mPackagePreferences.clear()
                println("mPackagePreferences cleared")
            }
        })
    }
}

fun println(msg: String) {
    Log.d(TAG, msg)
}

fun println(msg: Any?) {
    Log.d(TAG, msg.toString())
}
