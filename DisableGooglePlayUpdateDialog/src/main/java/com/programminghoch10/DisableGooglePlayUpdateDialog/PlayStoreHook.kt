package com.programminghoch10.DisableGooglePlayUpdateDialog

import android.app.Activity
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

const val PlayStorePackage = "com.android.vending"

class PlayStoreHook : IXposedHookLoadPackage {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName != PlayStorePackage) return
        val playCoreAcquisitionActivity = XposedHelpers.findClass(
            "com.google.android.finsky.playcoreacquisition.PlayCoreAcquisitionActivity",
            lpparam.classLoader,
        )
        val onCreateMethod = generateSequence(playCoreAcquisitionActivity) { it.superclass }.map { it.declaredMethods }
            .flatMap { it.asSequence() }
            .find { it.name == "onCreate" }
        XposedBridge.hookMethod(
            onCreateMethod,
            object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam) {
                    val activity = param.thisObject as Activity
                    activity.finish()
                }
            },
        )
    }
}
