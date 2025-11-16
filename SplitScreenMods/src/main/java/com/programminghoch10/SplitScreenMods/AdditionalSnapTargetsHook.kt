package com.programminghoch10.SplitScreenMods

import android.content.res.Resources
import android.graphics.Rect
import android.os.Build
import com.programminghoch10.SplitScreenMods.AdditionalSnapTargetsHookConfig.enabled
import com.programminghoch10.SplitScreenMods.BuildConfig.SHARED_PREFERENCES_NAME
import de.binarynoise.logger.Logger.log
import de.robv.android.xposed.IXposedHookInitPackageResources
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XSharedPreferences
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_InitPackageResources
import de.robv.android.xposed.callbacks.XC_LoadPackage

object AdditionalSnapTargetsHookConfig {
    val enabled = SnapModeHookConfig.enabled && CustomFixedRatioHookConfig.enabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.BAKLAVA
}

class AdditionalSnapTargetsHook : IXposedHookLoadPackage, IXposedHookInitPackageResources {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName != "com.android.systemui") return
        if (!enabled) return
        log("handleLoadPackage(${lpparam.packageName} in process ${lpparam.processName})")
        val preferences = XSharedPreferences(BuildConfig.APPLICATION_ID, SHARED_PREFERENCES_NAME)
        val selectedSnapTargetsString = preferences.getString("SnapTargets", "SYSTEM")!!
        if (selectedSnapTargetsString == "SYSTEM" || selectedSnapTargetsString == "CUSTOM") return
        val selectedSnapTargets = selectedSnapTargetsString.split(",").map { it.toFloat() }.sorted().drop(1)
        log("additional snap targets are ${selectedSnapTargets.joinToString()}")
        
        if (selectedSnapTargets.isEmpty()) return // handled by resource hook below
        
        val DividerSnapAlgorithmClass = XposedHelpers.findClass("com.android.wm.shell.common.split.DividerSnapAlgorithm", lpparam.classLoader)
        val SnapTargetClass = XposedHelpers.findClass(DividerSnapAlgorithmClass.name + "\$SnapTarget", lpparam.classLoader)
        val SnapTargetClassConstructor = XposedHelpers.findConstructorExact(SnapTargetClass, Int::class.java, Int::class.java)
        
        XposedBridge.hookAllConstructors(DividerSnapAlgorithmClass, object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                log("after ${DividerSnapAlgorithmClass.simpleName} constructor")
                val res = param.args[0] as Resources
                val mTargets = XposedHelpers.getObjectField(param.thisObject, "mTargets") as ArrayList<Any?>
                if (mTargets.isEmpty()) return
                
                // reimplementation of inlined methods getStartInset() and getEndInset()
                val mIsLeftRightSplit = XposedHelpers.getBooleanField(param.thisObject, "mIsLeftRightSplit")
                val mInsets = XposedHelpers.getObjectField(param.thisObject, "mInsets") as Rect
                val startInset = if (mIsLeftRightSplit) mInsets.left else mInsets.top
                val endInset = if (mIsLeftRightSplit) mInsets.right else mInsets.bottom
                
                // basically reimplemented addFixedDivisionTargets
                val mDisplayWidth = XposedHelpers.getIntField(param.thisObject, "mDisplayWidth")
                val mDisplayHeight = XposedHelpers.getIntField(param.thisObject, "mDisplayHeight")
                val start = startInset
                val end = if (mIsLeftRightSplit) mDisplayWidth - endInset else mDisplayHeight - endInset
                val mDividerSize = XposedHelpers.getIntField(param.thisObject, "mDividerSize")
                val mCalculateRatiosBasedOnAvailableSpace = getCalculateRatiosBasedOnAvailableSpace(res)
                log("mCalculateRatiosBasedOnAvailableSpace=$mCalculateRatiosBasedOnAvailableSpace")
                val mMinimalSizeResizableTask = XposedHelpers.getIntField(param.thisObject, "mMinimalSizeResizableTask")
                log("mMinimalSizeResizableTask=$mMinimalSizeResizableTask")
                fun getSize(ratio: Float): Int {
                    var size = (ratio * (end - start)).toInt() - mDividerSize / 2
                    if (mCalculateRatiosBasedOnAvailableSpace) size = Math.max(size, mMinimalSizeResizableTask)
                    return size
                }
                
                for (snapTargetRatio in selectedSnapTargets) {
                    val size = getSize(snapTargetRatio)
                    val startPosition = start + size
                    val endPosition = end - size
                    
                    // slightly changed reimplementation of inlined method maybeAddTarget()
                    fun maybeAddTarget(position: Int, size: Int, snapPosition: Int?) {
                        if (size < mMinimalSizeResizableTask) {
                            log("Cannot add snap target ${snapTargetRatio} because it's size of ${size} would be less than minimal size ${mMinimalSizeResizableTask}!")
                            return
                        }
                        log("adding snap target ${snapTargetRatio} at $position of size $size")
                        val snapTarget = SnapTargetClassConstructor.newInstance(position, snapPosition ?: SNAP_TO_NONE)
                        mTargets.add(snapTarget)
                    }
                    
                    maybeAddTarget(startPosition, startPosition - startInset, SNAP_TO_2_33_66)
                    maybeAddTarget(endPosition, end - endPosition, SNAP_TO_2_66_33)
                }
                
                log("final mTargets[${mTargets.size}]: ${mTargets.joinToString()}")
            }
        })
    }
    
    fun getCalculateRatiosBasedOnAvailableSpace(res: Resources): Boolean {
        val mCalculateRatiosBasedOnAvailableSpaceId = res.getIdentifier("config_flexibleSplitRatios", "bool", "android")
        return res.getBoolean(mCalculateRatiosBasedOnAvailableSpaceId)
    }
    
    override fun handleInitPackageResources(resparam: XC_InitPackageResources.InitPackageResourcesParam) {
        if (resparam.packageName != "com.android.systemui") return
        if (!enabled) return
        log("handleInitPackageResources(${resparam.packageName})")
        val preferences = XSharedPreferences(BuildConfig.APPLICATION_ID, SHARED_PREFERENCES_NAME)
        val selectedSnapTargetsString = preferences.getString("SnapTargets", "SYSTEM")!!
        if (selectedSnapTargetsString == "SYSTEM" || selectedSnapTargetsString == "CUSTOM") return
        val selectedSnapTargets = selectedSnapTargetsString.split(",")
        if (selectedSnapTargets.isEmpty()) return
        val customRatio = selectedSnapTargets.map { it.toFloat() }.minOf { it }
        log("min split ratio is ${customRatio}")
        if (customRatio < 0 || customRatio >= 0.5f) return
        CustomFixedRatioHook.overrideFixedRatio(resparam, customRatio)
    }
}
