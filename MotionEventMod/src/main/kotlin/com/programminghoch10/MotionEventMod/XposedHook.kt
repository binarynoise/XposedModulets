package com.programminghoch10.MotionEventMod

import android.view.MotionEvent
import android.view.View
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam

private const val hover_timeout = 1000L

class XposedHook : IXposedHookLoadPackage {
    private var hover_exit_timestamp: Long = 0
    
    override fun handleLoadPackage(lpparam: LoadPackageParam) {
        XposedHelpers.findAndHookMethod(
            View::class.java,
            "dispatchTouchEvent",
            MotionEvent::class.java,
            object : XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam) {
                    val event = param.args[0] as MotionEvent
                    if (event.getToolType(0) == MotionEvent.TOOL_TYPE_STYLUS) return
                    when (event.action) {
                        MotionEvent.ACTION_DOWN, MotionEvent.ACTION_UP, MotionEvent.ACTION_MOVE -> {
                            if (hover_exit_timestamp + hover_timeout > System.currentTimeMillis()) {
                                param.setResult(true)
                            }
                        }
                    }
                }
            },
        )
        
        XposedHelpers.findAndHookMethod(
            View::class.java,
            "dispatchHoverEvent",
            MotionEvent::class.java,
            object : XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam) {
                    val event = param.args[0] as MotionEvent
                    when (event.action) {
                        MotionEvent.ACTION_HOVER_ENTER -> {}
                        MotionEvent.ACTION_HOVER_EXIT -> hover_exit_timestamp = System.currentTimeMillis()
                    }
                }
            },
        )
    }
}
