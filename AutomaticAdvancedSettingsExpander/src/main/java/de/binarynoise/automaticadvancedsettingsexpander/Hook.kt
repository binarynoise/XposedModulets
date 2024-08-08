package de.binarynoise.automaticadvancedsettingsexpander

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty
import android.util.Log
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import de.robv.android.xposed.XC_MethodHook as MethodHook

const val TAG = "AutomaticSettingsExpand"

class Hook : IXposedHookLoadPackage {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        val packages = arrayOf(
            "androidx.preference",
            "android.preference",
            "android.support.v7.preference",
        )
        
        val classes = arrayOf("PreferenceGroup")
        
        val hook = object : MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                Log.d(TAG, "expandedCount would have been set to ${param.args[0]}, but setting to Int.MAX_VALUE instead")
                param.args[0] = Int.MAX_VALUE
            }
        }
        
        packages.forEach { p ->
            classes.forEach { c ->
                try {
                    XposedHelpers.findAndHookMethod(
                        "$p.$c",
                        lpparam.classLoader,
                        "setInitialExpandedChildrenCount",
                        Int::class.javaPrimitiveType,
                        hook,
                    )
                    Log.d(TAG, "Hooked $p.$c")
                } catch (_: Throwable) {
                }
            }
        }
    }
}
