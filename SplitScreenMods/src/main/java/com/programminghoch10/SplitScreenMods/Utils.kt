package com.programminghoch10.SplitScreenMods

import android.os.Build
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers

/**
 * hook after `DividerSnapAlgorithm` constructor
 * this is needed because [Build.VERSION_CODES.BAKLAVA] removed the constructor entirely,
 * so we hook the calling function instead
 */
fun hookDividerSnapAlgorithmAfterConstructor(classLoader: ClassLoader, methodHook: XC_MethodHook) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.BAKLAVA) {
        val SplitLayoutClass = XposedHelpers.findClass("com.android.wm.shell.common.split.SplitLayout", classLoader)
        XposedHelpers.findAndHookMethod(SplitLayoutClass, "updateLayouts", object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                val mDividerSnapAlgorithm = XposedHelpers.getObjectField(param.thisObject, "mDividerSnapAlgorithm")
                //param.thisObject = mDividerSnapAlgorithm
                val newParam = XposedHelpers.newInstance(MethodHookParam::class.java) as MethodHookParam
                newParam.thisObject = mDividerSnapAlgorithm
                XposedHelpers.callMethod(methodHook, "afterHookedMethod", newParam)
            }
        })
    } else {
        val DividerSnapAlgorithmClass = XposedHelpers.findClass("com.android.wm.shell.common.split.DividerSnapAlgorithm", classLoader)
        XposedBridge.hookAllConstructors(DividerSnapAlgorithmClass, methodHook)
    }
}
