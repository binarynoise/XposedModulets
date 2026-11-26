package com.programminghoch10.DisableGooglePlayUpdateDialog

import android.app.Activity
import android.content.Intent
import android.util.ArrayMap
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam

class Hook : IXposedHookLoadPackage {
    override fun handleLoadPackage(lpparam: LoadPackageParam) {
        Activity::class.java.declaredMethods.filter { it.name == "startActivity" }.forEach {
                XposedBridge.hookMethod(it, object : XC_MethodHook() {
                    override fun beforeHookedMethod(param: MethodHookParam) {
                        val intent = param.args[0] as Intent
                        val extras = intent.extras ?: return
                        val extrasMap = XposedHelpers.getObjectField(extras, "mMap") as ArrayMap<String, Object>
                        val resultReceiver = extrasMap["result_receiver"] ?: return
                        val packageName = resultReceiver::class.java.`package`?.name ?: return
                        if (packageName.startsWith("com.google.android.play.core.appupdate")) param.result = null
                    }
                })
            }
    }
}
