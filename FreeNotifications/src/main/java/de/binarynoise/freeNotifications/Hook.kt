package de.binarynoise.freeNotifications

import android.app.NotificationChannel
import android.app.NotificationChannelGroup
import android.os.Build
import de.binarynoise.logger.Logger.log
import de.binarynoise.reflection.findDeclaredField
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodReplacement.DO_NOTHING
import de.robv.android.xposed.XC_MethodReplacement.returnConstant
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import de.robv.android.xposed.XC_MethodHook as MethodHook

class Hook : IXposedHookLoadPackage {
    
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        
        fun hookVariable(
            clazz: Class<*>,
            variableName: String,
            setMethodName: String,
            getMethodName: String,
            value: Boolean,
        ) {
            tryAndLog("${clazz.simpleName} after constructor $variableName") {
                val mVariable = clazz.findDeclaredField(variableName)
                XposedBridge.hookAllConstructors(clazz, object : MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam) {
                        mVariable.set(param.thisObject, value)
                    }
                })
            }
            tryAndLog("${clazz.simpleName} $getMethodName") {
                XposedHelpers.findAndHookMethod(clazz, getMethodName, returnConstant(value))
            }
            tryAndLog("${clazz.simpleName} $setMethodName") {
                XposedHelpers.findAndHookMethod(clazz, setMethodName, Boolean::class.java, DO_NOTHING)
            }
        }
        
        fun String.isCharUpperCase(index: Int): Boolean {
            return this[index] == this[index].uppercaseChar()
        }
        
        /**
         * Remove prefix only if it is used in a CamelCase sentence,
         * only if the letter following the prefix is uppercase.
         */
        fun String.removeCamelCasePrefix(prefix: String): String {
            if (!this.isCharUpperCase(prefix.length + 1)) return this.removePrefix(prefix)
            return this
        }
        
        fun sanitizeVariableName(name: String): String {
            return name.removeCamelCasePrefix("m")
                .removeCamelCasePrefix("set")
                .removeCamelCasePrefix("is")
                .removeCamelCasePrefix("get")
                .replaceFirstChar { it.uppercaseChar() }
        }
        
        fun hookVariable(clazz: Class<*>, variableName: String, functionName: String, value: Boolean) {
            val sanitizedVariableName = sanitizeVariableName(variableName)
            val sanitizedFunctionName = sanitizeVariableName(functionName)
            return hookVariable(
                clazz,
                "m$sanitizedVariableName",
                "set$sanitizedFunctionName",
                "is$sanitizedFunctionName",
                value,
            )
        }
        
        fun hookVariable(clazz: Class<*>, name: String, value: Boolean) {
            return hookVariable(clazz, name, name, value)
        }
        
        // AOSP
        hookVariable(
            NotificationChannel::class.java,
            "mBlockableSystem",
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) "setBlockable" else "setBlockableSystem",
            true,
        )
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // AOSP
            hookVariable(
                NotificationChannel::class.java,
                "mImportanceLockedDefaultApp",
                "setImportanceLockedByCriticalDeviceFunction",
                false,
            )
        }
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
            // AOSP
            hookVariable(
                NotificationChannel::class.java,
                "mImportanceLockedByOEM",
                false,
            )
        }
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            // AOSP
            hookVariable(
                NotificationChannelGroup::class.java,
                "mBlocked",
                false,
            )
        }
    }
}

inline fun tryAndLog(message: String, block: () -> Unit) {
    return try {
        block()
        log("hook $message successful!")
    } catch (t: Throwable) {
        log("hook $message failed!", t)
        XposedBridge.log(t)
    }
}
