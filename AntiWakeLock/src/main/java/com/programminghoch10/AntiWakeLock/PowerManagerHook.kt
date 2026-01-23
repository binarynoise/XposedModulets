package com.programminghoch10.AntiWakeLock

import java.util.concurrent.*
import android.os.Build
import android.os.PowerManager
import android.os.WorkSource
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodReplacement.DO_NOTHING
import de.robv.android.xposed.XC_MethodReplacement.returnConstant
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

class PowerManagerHook : IXposedHookLoadPackage {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        XposedHelpers.findAndHookMethod(PowerManager.WakeLock::class.java, "acquire", DO_NOTHING)
        XposedHelpers.findAndHookMethod(PowerManager.WakeLock::class.java, "acquire", Int::class.java, DO_NOTHING)
        
        // optional hooks for completeness
        XposedHelpers.findAndHookMethod(PowerManager.WakeLock::class.java, "isHeld", returnConstant(false))
        XposedHelpers.findAndHookMethod(PowerManager.WakeLock::class.java, "release", DO_NOTHING)
        XposedHelpers.findAndHookMethod(PowerManager.WakeLock::class.java, "release", Int::class.java, DO_NOTHING)
        XposedHelpers.findAndHookMethod(PowerManager.WakeLock::class.java, "setReferenceCounted", Boolean::class.java, DO_NOTHING)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) XposedHelpers.findAndHookMethod(
            PowerManager.WakeLock::class.java,
            "setStateListener",
            Executor::class.java,
            PowerManager.WakeLockStateListener::class.java,
            DO_NOTHING,
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) XposedHelpers.findAndHookMethod(
            PowerManager.WakeLock::class.java,
            "setWorkSource",
            WorkSource::class.java,
            DO_NOTHING,
        )
    }
}
