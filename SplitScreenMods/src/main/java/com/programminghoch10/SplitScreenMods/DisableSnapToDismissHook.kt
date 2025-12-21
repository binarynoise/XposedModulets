package com.programminghoch10.SplitScreenMods

import android.os.Build
import com.programminghoch10.SplitScreenMods.BuildConfig.SHARED_PREFERENCES_NAME
import com.programminghoch10.SplitScreenMods.DisableSnapToDismissHookConfig.enabled
import de.binarynoise.logger.Logger.log
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XSharedPreferences
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

object DisableSnapToDismissHookConfig {
    val enabled = Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE
}

class DisableSnapToDismissHook : IXposedHookLoadPackage {
    
    fun isDismissTarget(snapPosition: Int): Boolean {
        log("isDismissTarget($snapPosition) = ${snapPosition == SNAP_TO_START_AND_DISMISS || snapPosition == SNAP_TO_END_AND_DISMISS}")
        return snapPosition == SNAP_TO_START_AND_DISMISS || snapPosition == SNAP_TO_END_AND_DISMISS
    }
    
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName != "com.android.systemui") return
        if (!enabled) return
        log("handleLoadPackage(${lpparam.packageName} in process ${lpparam.processName})")
        val preferences = XSharedPreferences(BuildConfig.APPLICATION_ID, SHARED_PREFERENCES_NAME)
        val enabled = preferences.getBoolean("DisableSnapToDismiss", false)
        if (!enabled) return
        
        val DividerSnapAlgorithmClass = XposedHelpers.findClass("com.android.wm.shell.common.split.DividerSnapAlgorithm", lpparam.classLoader)
        XposedBridge.hookAllConstructors(DividerSnapAlgorithmClass, object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                val mTargets = XposedHelpers.getObjectField(param.thisObject, "mTargets") as ArrayList<Any?>
                if (mTargets.isEmpty()) return
                log("filtering snap targets")
                val filteredTargets = mTargets.filter { !isDismissTarget(XposedHelpers.getIntField(it, "snapPosition")) }
                XposedHelpers.setObjectField(param.thisObject, "mTargets", filteredTargets)
            }
        })
    }
}
