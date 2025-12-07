package de.binarynoise.freeNotifications

import android.app.NotificationChannel
import android.app.NotificationChannelGroup
import android.content.Context
import android.content.pm.ApplicationInfo
import android.os.Build
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XC_MethodReplacement.DO_NOTHING
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

class SettingsHook : IXposedHookLoadPackage {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName != "com.android.settings") return
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val NotificationPreferenceControllerClass = Class.forName(
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) "com.android.settings.notification.app.NotificationPreferenceController"
                else "com.android.settings.notification.NotificationPreferenceController",
                false,
                lpparam.classLoader,
            )
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                tryAndLog("isAppBlockable") {
                    XposedHelpers.findAndHookMethod(
                        NotificationPreferenceControllerClass,
                        "isAppBlockable",
                        XC_MethodReplacement.returnConstant(true),
                    )
                }
                tryAndLog("${NotificationPreferenceControllerClass.simpleName} constructor overrideBlock overrideConfigure") {
                    XposedBridge.hookAllConstructors(
                        NotificationPreferenceControllerClass,
                        object : XC_MethodHook() {
                            override fun afterHookedMethod(param: MethodHookParam) {
                                listOf("Block", "Configure").forEach {
                                    XposedHelpers.setBooleanField(param.thisObject, "overrideCan$it", true)
                                    XposedHelpers.setBooleanField(param.thisObject, "overrideCan${it}Value", true)
                                }
                            }
                        },
                    )
                }
                tryAndLog("setOverrideCanBlock") {
                    XposedHelpers.findAndHookMethod(
                        NotificationPreferenceControllerClass,
                        "setOverrideCanBlock",
                        Boolean::class.java,
                        DO_NOTHING,
                    )
                }
                tryAndLog("setOverrideCanConfigure") {
                    XposedHelpers.findAndHookMethod(
                        NotificationPreferenceControllerClass,
                        "setOverrideCanConfigure",
                        Boolean::class.java,
                        DO_NOTHING,
                    )
                }
            }
            
            tryAndLog("isChannelBlockable") {
                XposedHelpers.findAndHookMethod(
                    NotificationPreferenceControllerClass,
                    "isChannelBlockable",
                    XC_MethodReplacement.returnConstant(true),
                )
            }
            tryAndLog("isChannelBlockable") {
                XposedHelpers.findAndHookMethod(
                    NotificationPreferenceControllerClass,
                    "isChannelBlockable",
                    NotificationChannel::class.java,
                    XC_MethodReplacement.returnConstant(true),
                )
            }
            tryAndLog("isChannelConfigurable") {
                XposedHelpers.findAndHookMethod(
                    NotificationPreferenceControllerClass,
                    "isChannelConfigurable",
                    NotificationChannel::class.java,
                    XC_MethodReplacement.returnConstant(true),
                )
            }
            tryAndLog("isChannelGroupBlockable") {
                XposedHelpers.findAndHookMethod(
                    NotificationPreferenceControllerClass,
                    "isChannelGroupBlockable",
                    XC_MethodReplacement.returnConstant(true),
                )
            }
            tryAndLog("isChannelGroupBlockable") {
                XposedHelpers.findAndHookMethod(
                    NotificationPreferenceControllerClass,
                    "isChannelGroupBlockable",
                    NotificationChannelGroup::class.java,
                    XC_MethodReplacement.returnConstant(true),
                )
            }
        }
        
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            val NotificationBackendClass = Class.forName("com.android.settings.notification.NotificationBackend", false, lpparam.classLoader)
            val AppRowClass = Class.forName(NotificationBackendClass.name + "\$AppRow", false, lpparam.classLoader)
            tryAndLog("loadAppRow") {
                NotificationBackendClass.declaredMethods.filter { it.name == "loadAppRow" }.forEach { method ->
                    XposedBridge.hookMethod(
                        method,
                        object : XC_MethodHook() {
                            override fun afterHookedMethod(param: MethodHookParam) {
                                val row = param.result
                                XposedHelpers.setBooleanField(row, "systemApp", false)
                            }
                        },
                    )
                }
            }
            tryAndLog("markAppRowWithBlockables") {
                XposedHelpers.findAndHookMethod(
                    NotificationBackendClass,
                    "markAppRowWithBlockables",
                    Array<String>::class.java,
                    AppRowClass,
                    String::class.java,
                    object : XC_MethodHook() {
                        override fun afterHookedMethod(param: MethodHookParam) {
                            val row = param.args[1]
                            XposedHelpers.setBooleanField(row, "systemApp", false)
                        }
                    },
                )
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                tryAndLog("recordCanBeBlocked") {
                    XposedBridge.hookMethod(
                        NotificationBackendClass.declaredMethods.single { it.name == "recordCanBeBlocked" },
                        object : XC_MethodHook() {
                            override fun afterHookedMethod(param: MethodHookParam) {
                                val row = param.args.last()
                                XposedHelpers.setBooleanField(row, "systemApp", false)
                            }
                        },
                    )
                }
                tryAndLog("isSystemApp") {
                    XposedHelpers.findAndHookMethod(
                        NotificationBackendClass,
                        "isSystemApp",
                        Context::class.java,
                        ApplicationInfo::class.java,
                        XC_MethodReplacement.returnConstant(false),
                    )
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                tryAndLog("isBlockable") {
                    XposedHelpers.findAndHookMethod(
                        NotificationBackendClass,
                        "isBlockable",
                        Context::class.java,
                        ApplicationInfo::class.java,
                        XC_MethodReplacement.returnConstant(true),
                    )
                }
            }
        }
    }
}
