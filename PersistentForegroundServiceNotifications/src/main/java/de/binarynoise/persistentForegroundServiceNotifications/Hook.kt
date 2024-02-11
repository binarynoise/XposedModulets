package de.binarynoise.persistentForegroundServiceNotifications

import android.annotation.SuppressLint
import android.app.Notification
import android.app.Notification.FLAG_AUTO_CANCEL
import android.app.Notification.FLAG_BUBBLE
import android.app.Notification.FLAG_FOREGROUND_SERVICE
import android.app.Notification.FLAG_GROUP_SUMMARY
import android.app.Notification.FLAG_HIGH_PRIORITY
import android.app.Notification.FLAG_INSISTENT
import android.app.Notification.FLAG_LOCAL_ONLY
import android.app.Notification.FLAG_NO_CLEAR
import android.app.Notification.FLAG_ONGOING_EVENT
import android.app.Notification.FLAG_ONLY_ALERT_ONCE
import android.app.Notification.FLAG_SHOW_LIGHTS
import android.service.notification.StatusBarNotification
import de.binarynoise.logger.Logger.log
import de.binarynoise.reflection.method
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import de.robv.android.xposed.XC_MethodHook as MethodHook

val FLAG_NO_DISMISS = XposedHelpers.getStaticIntField(Notification::class.java, "FLAG_NO_DISMISS")

@SuppressLint("PrivateApi", "MissingPermission")
class Hook : IXposedHookLoadPackage {
    
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) = try {
        val statusBarNotificationClass = StatusBarNotification::class.java
        val isNonDismissableMethod by method(statusBarNotificationClass)
        
        XposedBridge.hookMethod(isNonDismissableMethod, object : MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                with(param) {
                    val sbn = thisObject as StatusBarNotification
                    val isForeground = (sbn.notification.flags and FLAG_FOREGROUND_SERVICE) != 0
                    val isNoDismiss = (sbn.notification.flags and FLAG_NO_DISMISS) != 0
                    
                    val isNonDismissable = isNoDismiss || isForeground
                    log("isNonDismissable: ${sbn.packageName}: ${sbn.notification.flags.flagsToString()} -> isNonDismissable: $isNonDismissable")
                    
                    result = isNonDismissable
                }
            }
        })
        
        log("hooked isNonDismissableMethod")
    } catch (e: Exception) {
        log("Failed to hook isOngoingMethod", e)
    }
}

@Suppress("DEPRECATION")
fun Int.flagsToString(): String {
    val showLights = this and FLAG_SHOW_LIGHTS != 0
    val ongoingEvent = this and FLAG_ONGOING_EVENT != 0
    val insistent = this and FLAG_INSISTENT != 0
    val onlyAlertOnce = this and FLAG_ONLY_ALERT_ONCE != 0
    val autoCancel = this and FLAG_AUTO_CANCEL != 0
    val noClear = this and FLAG_NO_CLEAR != 0
    val foregroundService = this and FLAG_FOREGROUND_SERVICE != 0
    val highPriority = this and FLAG_HIGH_PRIORITY != 0
    val localOnly = this and FLAG_LOCAL_ONLY != 0
    val groupSummary = this and FLAG_GROUP_SUMMARY != 0
    val bubble = this and FLAG_BUBBLE != 0
    val noDismiss = this and FLAG_NO_DISMISS != 0
    
    return buildList {
        if (showLights) this.add("showLights")
        if (ongoingEvent) this.add("ongoingEvent")
        if (insistent) this.add("insistent")
        if (onlyAlertOnce) this.add("onlyAlertOnce")
        if (autoCancel) this.add("autoCancel")
        if (noClear) this.add("noClear")
        if (foregroundService) this.add("foregroundService")
        if (highPriority) this.add("highPriority")
        if (localOnly) this.add("localOnly")
        if (groupSummary) this.add("groupSummary")
        if (bubble) this.add("bubble")
        if (noDismiss) this.add("noDismiss")
    }.joinToString(", ", prefix = "[", postfix = "]")
}
