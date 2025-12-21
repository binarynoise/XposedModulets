package com.programminghoch10.SplitScreenMods

import android.content.res.AssetManager
import android.content.res.Configuration
import android.content.res.Resources
import android.content.res.XResForwarder
import android.util.DisplayMetrics
import com.programminghoch10.SplitScreenMods.BuildConfig.SHARED_PREFERENCES_NAME
import com.programminghoch10.SplitScreenMods.CustomFixedRatioHookConfig.enabled
import de.binarynoise.logger.Logger.log
import de.robv.android.xposed.IXposedHookInitPackageResources
import de.robv.android.xposed.XSharedPreferences
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_InitPackageResources

object CustomFixedRatioHookConfig {
    val enabled = SnapModeHookConfig.enabled
}

class CustomFixedRatioHook : IXposedHookInitPackageResources {
    override fun handleInitPackageResources(resparam: XC_InitPackageResources.InitPackageResourcesParam) {
        if (resparam.packageName != "com.android.systemui") return
        if (!enabled) return
        log("handleInitPackageResources(${resparam.packageName})")
        val preferences = XSharedPreferences(BuildConfig.APPLICATION_ID, SHARED_PREFERENCES_NAME)
        if (preferences.getString("SnapMode", "SYSTEM") != "FIXED") return
        val selectedSnapTargetsString = preferences.getString("SnapTargets", "SYSTEM")!!
        if (selectedSnapTargetsString != "CUSTOM") return
        if (!preferences.contains("CustomRatio")) return
        val customFixedRatio = preferences.getInt("CustomRatio", 33) / 100f
        overrideFixedRatio(resparam, customFixedRatio)
    }
    
    companion object {
        fun overrideFixedRatio(resparam: XC_InitPackageResources.InitPackageResourcesParam, customFixedRatio: Float) {
            log("overriding fixed ratio with $customFixedRatio")
            resparam.res.setReplacement("android", "fraction", "docked_stack_divider_fixed_ratio", FractionReplacement(customFixedRatio))
        }
        
        fun FractionReplacement(fraction: Float): XResForwarder {
            val assetManager = XposedHelpers.getStaticObjectField(AssetManager::class.java, "sSystem") as AssetManager
            val res = object : Resources(assetManager, DisplayMetrics(), Configuration()) {
                override fun getFraction(id: Int, base: Int, pbase: Int): Float {
                    return fraction
                }
            }
            return XResForwarder(res, 0)
        }
    }
}
