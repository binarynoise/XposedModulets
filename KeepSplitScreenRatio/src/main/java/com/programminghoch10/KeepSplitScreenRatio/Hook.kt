package com.programminghoch10.KeepSplitScreenRatio

import android.os.IBinder
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import de.robv.android.xposed.XC_MethodHook as MethodHook

class Hook : IXposedHookLoadPackage {
    
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName != "com.android.systemui") return
        
        val SplitScreenTransitionsClass = XposedHelpers.findClass("com.android.wm.shell.splitscreen.SplitScreenTransitions", lpparam.classLoader)
        XposedHelpers.findAndHookMethod(
            SplitScreenTransitionsClass,
            "setEnterTransition",
            IBinder::class.java,
            "android.window.RemoteTransition",
            Int::class.java,
            Boolean::class.java,
            object : MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam) {
                    param.args[3] = false
                }
            },
        )
    }
}
