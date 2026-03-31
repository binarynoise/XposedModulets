package de.binarynoise.HideAbi

import java.lang.reflect.Method
import android.os.Build
import de.binarynoise.reflection.cast
import de.binarynoise.reflection.makeAccessible
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XSharedPreferences
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import org.lsposed.hiddenapibypass.HiddenApiBypass
import de.robv.android.xposed.XC_MethodHook as MethodHook

class Hook : IXposedHookLoadPackage {
    
    
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        val BuildClass = XposedHelpers.findClass("android.os.Build", lpparam.classLoader)
        val getStringList: Method = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            HiddenApiBypass.getDeclaredMethod(BuildClass, "getStringList", String::class.java, String::class.java)
        } else {
            BuildClass.getDeclaredMethod("getStringList", String::class.java, String::class.java)
        }
        getStringList.makeAccessible()
        
        val prefs = XSharedPreferences(BuildConfig.APPLICATION_ID, BuildConfig.SHARED_PREFERENCES_NAME)
        
        XposedBridge.hookMethod(
            getStringList,
            object : MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam) {
                    with(param) {
                        val property = args.first().cast<String>()
                        val abiCategory = AbiCategory.fromProperty(property) ?: return
                        
                        val result = param.result.cast<Array<String>>()
                        val filtered = result.filterNot { abi ->
                            prefs.getBoolean(abiCategory.getPreferenceKeyFor(abi), false)
                        }
                        param.result = filtered.toTypedArray()
                    }
                }
            },
        )
        
        AbiCategory.entries.forEach { entry ->
            XposedHelpers.setStaticObjectField(Build::class.java, entry.name, getStringList(null, entry.property, ","))
        }
    }
}
