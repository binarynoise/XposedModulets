package com.programminghoch10.SplitScreenMods

import android.os.Build
import android.os.IBinder
import com.programminghoch10.SplitScreenMods.BuildConfig.SHARED_PREFERENCES_NAME
import com.programminghoch10.SplitScreenMods.KeepSplitScreenRatioHookConfig.enabled
import de.binarynoise.logger.Logger.log
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XSharedPreferences
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import de.robv.android.xposed.XC_MethodHook as MethodHook


object KeepSplitScreenRatioHookConfig {
    @JvmField
    val enabled = Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE
}

class KeepSplitScreenRatioHook : IXposedHookLoadPackage {
    
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName != "com.android.systemui") return
        if (!enabled) return
        log("handleLoadPackage(${lpparam.packageName} in process ${lpparam.processName})")
        val preferences = XSharedPreferences(BuildConfig.APPLICATION_ID, SHARED_PREFERENCES_NAME)
        val enabled = preferences.getBoolean("KeepSplitScreenRatio", false)
        if (!enabled) return
        
        val SplitLayoutClass = XposedHelpers.findClass("com.android.wm.shell.common.split.SplitLayout", lpparam.classLoader)
        val StageCoordinatorClass = XposedHelpers.findClass("com.android.wm.shell.splitscreen.StageCoordinator", lpparam.classLoader)
        
        /*
        Currently this module disables SplitScreen enter and dismiss animations completely.
        
        Before animating, the Divider state is initialized in 
        https://github.com/LineageOS/android_frameworks_base/blob/2dc97cf3d6234c87497ca78b2734bb3ed604c349/libs/WindowManager/Shell/src/com/android/wm/shell/splitscreen/StageCoordinator.java#L1849
        then a "fling to center" is started in
        https://github.com/LineageOS/android_frameworks_base/blob/2dc97cf3d6234c87497ca78b2734bb3ed604c349/libs/WindowManager/Shell/src/com/android/wm/shell/splitscreen/StageCoordinator.java#L3586
        Since this "fling to center" is embedded deeply, 
        we currently simply disable it.
        */
        
        XposedHelpers.findAndHookMethod(
            StageCoordinatorClass,
            "onSnappedToDismiss",
            Int::class.java,
            Boolean::class.java,
            object : MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam) {
                    // reset the divider position after a fling to dismiss is finished,
                    // since the SplitScreen enter animation won't take care of it anymore
                    val mSplitLayout = XposedHelpers.getObjectField(param.thisObject, "mSplitLayout")
                    //XposedHelpers.callMethod(mSplitLayout, "resetDividerPosition")
                    XposedBridge.invokeOriginalMethod(
                        XposedHelpers.findMethodExact(
                            SplitLayoutClass,
                            "resetDividerPosition",
                            *arrayOf<Any>(),
                        ),
                        mSplitLayout,
                        arrayOf(),
                    )
                    log("${StageCoordinatorClass.simpleName} onSnappedToDismiss called")
                }
            },
        )
        
        // disable resetDividerPosition entirely
        XposedHelpers.findAndHookMethod(
            SplitLayoutClass,
            "resetDividerPosition",
            XC_MethodReplacement.DO_NOTHING,
        )
        
        XposedHelpers.findAndHookMethod(
            StageCoordinatorClass,
            "prepareSplitLayout",
            "android.window.WindowContainerTransaction",
            Boolean::class.java,
            object : MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam) {
                    // disabling resizeAnim makes the method call resetDividerPosition()
                    log("prepareSplitLayout: disable resizeAnim")
                    param.args[1] = false
                }
            },
        )
        
        val startPendingAnimationMethod =
            StageCoordinatorClass.declaredMethods.find { it.name == "startPendingAnimation" && it.parameterTypes[0] == IBinder::class.java }!!
        XposedBridge.hookMethod(
            startPendingAnimationMethod,
            object : MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam) {
                    val binder = param.args[0] as IBinder
                    val stageCoordinator = param.thisObject
                    val mSplitTransitions = XposedHelpers.getObjectField(stageCoordinator, "mSplitTransitions")
                    val isPendingEnter = XposedHelpers.callMethod(mSplitTransitions, "isPendingEnter", binder) as Boolean
                    log("${startPendingAnimationMethod.name}: isPendingEnter=$isPendingEnter")
                    if (!isPendingEnter) return
                    val mPendingEnter = XposedHelpers.getObjectField(mSplitTransitions, "mPendingEnter")
                    // disable resizeAnim when entering SplitScreen
                    XposedHelpers.setBooleanField(mPendingEnter, "mResizeAnim", false)
                }
            },
        )
    }
}
