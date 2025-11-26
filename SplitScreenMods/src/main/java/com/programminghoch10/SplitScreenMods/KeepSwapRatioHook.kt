package com.programminghoch10.SplitScreenMods

import java.util.function.*
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Rect
import android.os.Build
import android.view.SurfaceControl
import com.programminghoch10.SplitScreenMods.BuildConfig.SHARED_PREFERENCES_NAME
import com.programminghoch10.SplitScreenMods.KeepSwapRatioHookConfig.enabled
import de.binarynoise.logger.Logger.log
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XSharedPreferences
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

object KeepSwapRatioHookConfig {
    @JvmField
    val enabled = Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE
}

class KeepSwapRatioHook : IXposedHookLoadPackage {
    val SWAP_ANIMATION_TOTAL_DURATION = 500L
    
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName != "com.android.systemui") return
        if (!enabled) return
        log("handleLoadPackage(${lpparam.packageName} in process ${lpparam.processName})")
        val preferences = XSharedPreferences(BuildConfig.APPLICATION_ID, SHARED_PREFERENCES_NAME)
        val enabled = preferences.getBoolean("KeepSwapRatio", false)
        if (!enabled) return
        
        val SplitLayoutClass = XposedHelpers.findClass("com.android.wm.shell.common.split.SplitLayout", lpparam.classLoader)
        val playSwapAnimationMethod =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) SplitLayoutClass.declaredMethods.single { it.name == "playSwapAnimation" }
            else SplitLayoutClass.declaredMethods.single { it.name == "splitSwitching" }
        val getDisplayStableInsetsMethod = XposedHelpers.findMethodExact(SplitLayoutClass, "getDisplayStableInsets", Context::class.java)
        
        XposedBridge.hookMethod(
            playSwapAnimationMethod,
            object : XC_MethodReplacement() {
                override fun replaceHookedMethod(param: MethodHookParam): Any? {
                    @Suppress("UNCHECKED_CAST")
                    val finishCallback = param.args[3] as Consumer<Rect>
                    val mContext = XposedHelpers.getObjectField(param.thisObject, "mContext") as Context
                    val insets = getDisplayStableInsetsMethod.invoke(param.thisObject, mContext) as Rect
                    val mIsLeftRightSplit = XposedHelpers.getBooleanField(param.thisObject, "mIsLeftRightSplit")
                    insets.set(
                        if (mIsLeftRightSplit) insets.left else 0,
                        if (mIsLeftRightSplit) 0 else insets.top,
                        if (mIsLeftRightSplit) insets.right else 0,
                        if (mIsLeftRightSplit) 0 else insets.bottom,
                    )
                    //finishCallback.accept(insets)
                    log("replaced ${playSwapAnimationMethod.name} with dummy implementation to prevent swapping")
                    
                    val t = param.args[0] as SurfaceControl.Transaction
                    val topLeftStage = param.args[1]
                    val bottomRightStage = param.args[2]
                    
                    val shouldVeil = insets.left != 0 || insets.top != 0 || insets.right != 0 || insets.bottom != 0
                    
                    val endBounds1 = Rect()
                    val endBounds2 = Rect()
                    
                    // Compute destination bounds.
                    val dividerPos = XposedHelpers.getIntField(param.thisObject, "mDividerPosition")
                    XposedHelpers.callMethod(
                        param.thisObject,
                        "updateBounds",
                        dividerPos,
                        endBounds2,
                        endBounds1,
                        Rect(),
                        false, /* setEffectBounds */
                    )
                    
                    val mRootBounds = XposedHelpers.getObjectField(param.thisObject, "mRootBounds") as Rect
                    // Offset to real position under root container.
                    endBounds1.offset(-mRootBounds.left, -mRootBounds.top)
                    endBounds2.offset(-mRootBounds.left, -mRootBounds.top)
                    
                    fun moveSurface(stage: Any, startRect: Rect, endRect: Rect, offsetX: Float, offsetY: Float): ValueAnimator {
                        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) XposedHelpers.callMethod(
                            param.thisObject,
                            "moveSurface",
                            t,
                            stage,
                            startRect,
                            endRect,
                            offsetX,
                            offsetY,
                            true,  /* roundCorners */
                            true,  /* isGoingBehind */
                            shouldVeil,
                        ) as ValueAnimator
                        else XposedHelpers.callMethod(
                            param.thisObject,
                            "moveSurface",
                            t,
                            stage,
                            startRect,
                            endRect,
                            offsetX,
                            offsetY,
                        ) as ValueAnimator
                    }
                    
                    fun getRefBounds(topLeft: Boolean): Rect {
                        @Suppress("KotlinConstantConditions")
                        val methodName = when {
                            Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM && topLeft -> "getTopLeftBounds"
                            Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM && !topLeft -> "getBottomRightBounds"
                            Build.VERSION.SDK_INT <= Build.VERSION_CODES.UPSIDE_DOWN_CAKE && topLeft -> "getBounds1"
                            Build.VERSION.SDK_INT <= Build.VERSION_CODES.UPSIDE_DOWN_CAKE && !topLeft -> "getBounds2"
                            else -> throw IllegalStateException("no method name matched for getting bounds")
                        }
                        val bounds = XposedHelpers.callMethod(param.thisObject, methodName) as Rect
                        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) bounds.offset(-mRootBounds.left, -mRootBounds.top)
                        return bounds
                    }
                    
                    val topLeftBounds = getRefBounds(true)
                    val animator1 = moveSurface(
                        topLeftStage,
                        topLeftBounds,
                        endBounds1,
                        -insets.left.toFloat(),
                        -insets.top.toFloat(),
                    )
                    val bottomRightBounds = getRefBounds(false)
                    val animator2 = moveSurface(
                        bottomRightStage,
                        bottomRightBounds,
                        endBounds2,
                        insets.left.toFloat(),
                        insets.top.toFloat(),
                    )
                    
                    val mSwapAnimator = AnimatorSet()
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
                        XposedHelpers.setObjectField(param.thisObject, "mSwapAnimator", mSwapAnimator)
                    }
                    mSwapAnimator.playTogether(animator1, animator2)
                    mSwapAnimator.duration = SWAP_ANIMATION_TOTAL_DURATION
                    mSwapAnimator.addListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            finishCallback.accept(insets)
                        }
                    })
                    mSwapAnimator.start()
                    return null
                }
            },
        )
    }
}
