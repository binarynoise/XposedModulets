package de.binarynoise.AutomaticAdvancedSettingsExpander

import de.binarynoise.logger.Logger.log
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import de.robv.android.xposed.XC_MethodHook as MethodHook


class PreferenceGroupChildrenHook : IXposedHookLoadPackage {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        val packages = arrayOf(
            "androidx.preference",
            "android.preference",
            "android.support.v7.preference",
        )
        
        val classes = arrayOf("PreferenceGroup")
        
        val hook = object : MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                log("expandedCount would have been set to ${param.args[0]}, but setting to Int.MAX_VALUE instead")
                param.args[0] = Int.MAX_VALUE
            }
        }
        
        packages.forEach { pkg ->
            classes.forEach { cls ->
                try {
                    XposedHelpers.findAndHookMethod("$pkg.$cls", lpparam.classLoader, "setInitialExpandedChildrenCount", Int::class.java, hook)
                    log("Hooked $pkg.$cls")
                } catch (_: NoSuchMethodError) {
                } catch (_: NoSuchMethodException) {
                } catch (_: XposedHelpers.ClassNotFoundError) {
                } catch (_: ClassNotFoundException) {
                } catch (e: Exception) {
                    log("failed to hook", e)
                }
            }
        }
    }
}
