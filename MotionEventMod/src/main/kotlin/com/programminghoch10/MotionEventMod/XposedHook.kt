package com.programminghoch10.MotionEventMod

import android.view.MotionEvent
import android.view.View
import com.programminghoch10.MotionEventMod.BuildConfig.APPLICATION_ID
import com.programminghoch10.MotionEventMod.BuildConfig.SHARED_PREFERENCES_NAME
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodHook.MethodHookParam
import de.robv.android.xposed.XSharedPreferences
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam

class XposedHook : IXposedHookLoadPackage {
    val sharedPreferences = XSharedPreferences(APPLICATION_ID, SHARED_PREFERENCES_NAME)
    val disableTouchDuringPen get() = sharedPreferences.getBoolean("disableTouchDuringPen", false)
    val disableTouchDuringHover get() = sharedPreferences.getBoolean("disableTouchDuringHover", false)
    val markAsHandled get() = sharedPreferences.getBoolean("markAsHandled", true)
    fun preventMotionEvent(param: MethodHookParam) = run { param.result = markAsHandled }
    
    private var isPenDown: Boolean = false
    private var isPenHovering: Boolean = false
    private var lastPenEventTimestamp: Long = 0L
    private var lastHoverEventTimestamp: Long = 0L
    
    fun handleTouchEvent(event: MotionEvent, param: MethodHookParam) {
        if (disableTouchDuringPen && isPenDown) preventMotionEvent(param)
        if (disableTouchDuringHover && isPenHovering) preventMotionEvent(param)
    }
    
    fun handleStylusEvent(event: MotionEvent, param: MethodHookParam) {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> isPenDown = true
            MotionEvent.ACTION_UP -> isPenDown = false
        }
        lastPenEventTimestamp = System.currentTimeMillis()
    }
    
    fun handleHoverEvent(event: MotionEvent, param: MethodHookParam) {
        when (event.action) {
            MotionEvent.ACTION_HOVER_ENTER -> isPenHovering = true
            MotionEvent.ACTION_HOVER_EXIT -> isPenHovering = false
        }
        lastHoverEventTimestamp = System.currentTimeMillis()
    }
    
    override fun handleLoadPackage(lpparam: LoadPackageParam) {
        if (lpparam.packageName == "android") return
        
        XposedHelpers.findAndHookMethod(
            View::class.java,
            "dispatchTouchEvent",
            MotionEvent::class.java,
            object : XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam) {
                    val event = param.args[0] as MotionEvent
                    val actionIndex = event.actionIndex
                    val pointerId = event.getPointerId(actionIndex)
                    val toolType = event.getToolType(pointerId)
                    when (toolType) {
                        MotionEvent.TOOL_TYPE_UNKNOWN -> return
                        MotionEvent.TOOL_TYPE_STYLUS, MotionEvent.TOOL_TYPE_ERASER -> handleStylusEvent(event, param)
                        MotionEvent.TOOL_TYPE_MOUSE -> TODO("implement mouse")
                        MotionEvent.TOOL_TYPE_FINGER -> handleTouchEvent(event, param)
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
                    handleHoverEvent(event, param)
                }
            },
        )
    }
}
