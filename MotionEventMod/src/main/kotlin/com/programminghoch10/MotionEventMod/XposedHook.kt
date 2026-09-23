package com.programminghoch10.MotionEventMod

import kotlin.math.roundToLong
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

val toolTypeFields = MotionEvent::class.java.declaredFields.filter { it.name.startsWith("TOOL_TYPE_") && it.type == Int::class.java }
val toolTypes = toolTypeFields.map { it.name }
val toolTypeNames = toolTypeFields.associate { it.getInt(null) to it.name }
fun toolTypeEnabledKey(toolType: String): String = "${toolType.lowercase()}_enabled"

class XposedHook : IXposedHookLoadPackage {
    val sharedPreferences = XSharedPreferences(APPLICATION_ID, SHARED_PREFERENCES_NAME)
    val disableTouchDuringPen get() = sharedPreferences.getBoolean("disableTouchDuringPen", false)
    val disableTouchDuringHover get() = sharedPreferences.getBoolean("disableTouchDuringHover", false)
    val markAsHandled get() = sharedPreferences.getBoolean("markAsHandled", true)
    val disableTouchTimeout get() = (sharedPreferences.getFloat("disableTouchTimeout", 0f) * 1000L).roundToLong()
    val disableHover get() = sharedPreferences.getBoolean("disableHover", false)
    fun preventMotionEvent(param: MethodHookParam) = run { param.result = markAsHandled }
    
    private var isPenDown: Boolean = false
    private var isPenHovering: Boolean = false
    private var lastPenEventTimestamp: Long = 0L
    private var lastHoverEventTimestamp: Long = 0L
    fun isInTimeout(m: Long): Boolean = System.currentTimeMillis() < m + disableTouchTimeout
    
    fun handleTouchEvent(event: MotionEvent, param: MethodHookParam) {
        if (disableTouchDuringPen && isPenDown) preventMotionEvent(param)
        if (disableTouchDuringHover && isPenHovering) preventMotionEvent(param)
        if (disableTouchDuringPen && isInTimeout(lastPenEventTimestamp)) preventMotionEvent(param)
        if (disableTouchDuringHover && isInTimeout(lastHoverEventTimestamp)) preventMotionEvent(param)
    }
    
    fun handleStylusEvent(event: MotionEvent, param: MethodHookParam) {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> isPenDown = true
            MotionEvent.ACTION_UP -> isPenDown = false
        }
        lastPenEventTimestamp = System.currentTimeMillis()
    }
    
    fun handleStylusHoverEvent(event: MotionEvent, param: MethodHookParam) {
        when (event.action) {
            MotionEvent.ACTION_HOVER_ENTER -> isPenHovering = true
            MotionEvent.ACTION_HOVER_EXIT -> isPenHovering = false
        }
        lastHoverEventTimestamp = System.currentTimeMillis()
    }
    
    fun shouldDisableMotionEventByToolType(toolType: Int): Boolean {
        val name = toolTypeNames[toolType] ?: return false
        val key = toolTypeEnabledKey(name)
        return !sharedPreferences.getBoolean(key, true)
    }
    
    override fun handleLoadPackage(lpparam: LoadPackageParam) {
        if (lpparam.packageName == "android") return
        if (lpparam.packageName == APPLICATION_ID) return
        
        XposedHelpers.findAndHookMethod(
            View::class.java,
            "dispatchTouchEvent",
            MotionEvent::class.java,
            object : XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam) {
                    val event = param.args[0] as MotionEvent
                    val toolType = event.getToolType()
                    if (shouldDisableMotionEventByToolType(toolType)) return preventMotionEvent(param)
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
                    if (shouldDisableMotionEventByToolType(event.getToolType())) return preventMotionEvent(param)
                    if (disableHover) return preventMotionEvent(param)
                    when (event.getToolType()) {
                        MotionEvent.TOOL_TYPE_UNKNOWN -> return
                        MotionEvent.TOOL_TYPE_STYLUS, MotionEvent.TOOL_TYPE_ERASER -> handleStylusHoverEvent(event, param)
                    }
                }
            },
        )
    }
}

fun MotionEvent.getPointerId(): Int = getPointerId(actionIndex)
fun MotionEvent.getToolType(): Int = getToolType(getPointerId())
