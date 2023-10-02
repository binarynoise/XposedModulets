package de.binarynoise.automaticadvancedsettingsexpander

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty
import android.util.Log
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

const val TAG = "AutomaticSettingsExpand"

class Hook : IXposedHookLoadPackage {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        val packages = arrayOf(
            "androidx.preference",
            "android.preference",
            "android.support.v7.preference",
        )
        
        val classes = arrayOf("PreferenceGroup")
        
        val hook = object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                var expandedCount by param.args(0)
                Log.d(TAG, "expandedCount would have been set to $expandedCount, but setting to Int.MAX_VALUE instead")
                expandedCount = Int.MAX_VALUE
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
    
    operator fun <T> Array<T>.invoke(index: Int): ArrayDelegate<T> {
        return ArrayDelegate(this, index)
    }
    
    class ArrayDelegate<T>(val array: Array<T>, val index: Int) : ReadWriteProperty<Any?, T> {
        override fun getValue(thisRef: Any?, property: KProperty<*>): T {
            return array[index]
        }
        
        override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
            array[index] = value
        }
    }
}
