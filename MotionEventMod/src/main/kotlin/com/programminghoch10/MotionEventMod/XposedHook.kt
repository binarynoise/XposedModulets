package com.programminghoch10.MotionEventMod

import java.lang.reflect.Method
import kotlin.math.roundToLong
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.programminghoch10.MotionEventMod.BuildConfig.APPLICATION_ID
import com.programminghoch10.MotionEventMod.BuildConfig.SHARED_PREFERENCES_NAME
import com.programminghoch10.MotionEventMod.BuildConfig.TAG
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodHook.MethodHookParam
import de.robv.android.xposed.XSharedPreferences
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam

val toolTypeFields = MotionEvent::class.java.declaredFields.filter { it.name.startsWith("TOOL_TYPE_") && it.type == Int::class.java }
val toolTypes = toolTypeFields.map { it.name }
val toolTypeNames = toolTypeFields.associate { it.getInt(null) to it.name }
fun toolTypeEnabledKey(toolType: String): String = "${toolType.lowercase()}_enabled"

class XposedHook : IXposedHookLoadPackage {
    val sharedPreferences = XSharedPreferences(APPLICATION_ID, SHARED_PREFERENCES_NAME)
    val disableTouchDuringPen get() = sharedPreferences.getBoolean("disableTouchDuringPen", false)
    val disableTouchDuringHover get() = disableTouchDuringPen && sharedPreferences.getBoolean("disableTouchDuringHover", false)
    val markAsHandled get() = sharedPreferences.getBoolean("markAsHandled", true)
    val disableTouchTimeoutMs
        get() = if (disableTouchDuringPen || disableTouchDuringHover) (sharedPreferences.getFloat("disableTouchTimeout", 0f) * 1000L).roundToLong()
        else 0L
    val disableHover get() = sharedPreferences.getBoolean("disableHover", false)
    val replayOngoingEvents get() = (disableTouchDuringPen || disableTouchDuringHover) && sharedPreferences.getBoolean("replayOngoingEvents", false)
    
    fun preventMotionEvent(param: MethodHookParam) = run { param.result = markAsHandled }
    
    private var isPenDown: Boolean = false
    private var isPenHovering: Boolean = false
    private var lastPenEventTimestamp: Long = 0L
    private var lastHoverEventTimestamp: Long = 0L
    fun isInTimeout(compareTime: Long, eventTime: Long): Boolean =
        disableTouchTimeoutMs > 0 && eventTime in compareTime..<compareTime + disableTouchTimeoutMs
    
    fun isInTimeout(eventTime: Long): Boolean = (disableTouchDuringPen && isInTimeout(lastPenEventTimestamp, eventTime)) // 
        || (disableTouchDuringHover && isInTimeout(lastHoverEventTimestamp, eventTime))
    
    fun shouldPreventTouchEvent(eventTime: Long): Boolean {
        if (disableTouchDuringPen && isPenDown) return true
        if (disableTouchDuringHover && isPenHovering) return true
        if (disableTouchDuringPen && !isPenDown && isInTimeout(lastPenEventTimestamp, eventTime)) return true
        if (disableTouchDuringHover && !isPenHovering && isInTimeout(lastHoverEventTimestamp, eventTime)) return true
        return false
    }
    
    private val motionEventStorage = mutableMapOf<Int, MutableList<MotionEvent>>()
    
    fun handleTouchEvent(event: MotionEvent, param: MethodHookParam, dispatchingView: View) {
        val event = event.deepClone()
        require(event.getToolType() == MotionEvent.TOOL_TYPE_FINGER)
        var preventTouchEvent = shouldPreventTouchEvent(event.eventTime)
        val pointerId = event.getPointerId()
        if (preventTouchEvent) when (event.action) {
            MotionEvent.ACTION_DOWN -> motionEventStorage[pointerId] = mutableListOf(event)
            MotionEvent.ACTION_MOVE -> motionEventStorage[pointerId]?.add(event)
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> motionEventStorage.remove(pointerId)
        }
        if (!preventTouchEvent && event.action in listOf(
                MotionEvent.ACTION_MOVE,
                MotionEvent.ACTION_UP,
                MotionEvent.ACTION_CANCEL,
            ) && motionEventStorage.contains(pointerId)
        ) {
            if (replayOngoingEvents) {
                Log.d(TAG, "handleTouchEvent: replaying ongoing MotionEvents size=${motionEventStorage[pointerId]?.size}")
                motionEventStorage[pointerId]?.forEach { dispatchingView.dispatchTouchEventUnhooked(it) }
                motionEventStorage.remove(pointerId)
            } else {
                //Log.d(TAG, "handleTouchEvent: prevent because started before enabled")
                preventTouchEvent = true
                if (event.action == MotionEvent.ACTION_UP) motionEventStorage.remove(pointerId)
            }
        }
        if (preventTouchEvent) preventMotionEvent(param)
    }
    
    fun handleStylusEvent(event: MotionEvent, param: MethodHookParam) {
        require(event.getToolType() in listOf(MotionEvent.TOOL_TYPE_STYLUS, MotionEvent.TOOL_TYPE_ERASER))
        when (event.action) {
            MotionEvent.ACTION_DOWN -> isPenDown = true
            MotionEvent.ACTION_UP -> isPenDown = false
        }
        lastPenEventTimestamp = event.eventTime
    }
    
    fun handleStylusHoverEvent(event: MotionEvent, param: MethodHookParam) {
        require(event.getToolType() in listOf(MotionEvent.TOOL_TYPE_STYLUS, MotionEvent.TOOL_TYPE_ERASER))
        require(event.action in listOf(MotionEvent.ACTION_HOVER_ENTER, MotionEvent.ACTION_HOVER_MOVE, MotionEvent.ACTION_HOVER_EXIT))
        when (event.action) {
            MotionEvent.ACTION_HOVER_ENTER -> isPenHovering = true
            MotionEvent.ACTION_HOVER_EXIT -> isPenHovering = false
        }
        lastHoverEventTimestamp = event.eventTime
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
                    val view = param.thisObject as View
                    val event = param.args[0] as MotionEvent
                    val toolType = event.getToolType()
                    if (shouldDisableMotionEventByToolType(toolType)) return preventMotionEvent(param)
                    when (toolType) {
                        MotionEvent.TOOL_TYPE_UNKNOWN -> return
                        MotionEvent.TOOL_TYPE_STYLUS, MotionEvent.TOOL_TYPE_ERASER -> handleStylusEvent(event, param)
                        MotionEvent.TOOL_TYPE_MOUSE -> TODO("implement mouse")
                        MotionEvent.TOOL_TYPE_FINGER -> handleTouchEvent(event, param, view)
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
fun MotionEvent.getToolType(): Int = getToolType(actionIndex)

private val dispatchTouchEventMethod: Method by lazy {
    XposedHelpers.findMethodExact(
        View::class.java,
        "dispatchTouchEvent",
        MotionEvent::class.java,
    )!!
}

fun View.dispatchTouchEventUnhooked(event: MotionEvent) {
    XposedBridge.invokeOriginalMethod(dispatchTouchEventMethod, this, arrayOf(event))
}
