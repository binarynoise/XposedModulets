package de.binarynoise.dontResetIfBootedAndConnected

import android.annotation.SuppressLint
import android.util.Log
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.XposedHelpers.ClassNotFoundError
import de.robv.android.xposed.callbacks.XC_LoadPackage

@SuppressLint("PrivateApi", "MissingPermission")
class Hook : IXposedHookLoadPackage {
    
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        try {
            XposedHelpers.findAndHookMethod(
                "com.android.server.StorageManagerService",
                lpparam.classLoader,
                "resetIfBootedAndConnected",
                object : XC_MethodHook() {
                    override fun beforeHookedMethod(param: MethodHookParam) {
                        param.result = Unit
                        Log.i("StorageManagerService", "If I wasn't told not to, I would've resetIfBootedAndConnected now.")
                    }
                },
            )
        } catch (_: ClassNotFoundError) {
        } catch (_: NoSuchMethodError) {
        }
    }
}
