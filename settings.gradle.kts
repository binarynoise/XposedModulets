@file:Suppress("UnstableApiUsage")

pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
                includeGroupAndSubgroups("androidx")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google {
            content {
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
                includeGroupAndSubgroups("androidx")
            }
        }
        maven("https://api.xposed.info/") {
            content {
                includeGroup("de.robv.android.xposed")
            }
        }
        mavenCentral()
//        maven("https://jitpack.io")
//        gradlePluginPortal()
    }
}


rootProject.name = "XposedModulets"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
include(":AnimationScaleMod")
include(":AntiBrightnessChange")
include(":AutomaticAdvancedSettingsExpander")
include(":BetterAnimationScaling")
include(":BetterVerboseWiFiLogging")
include(":BetterBluetoothDeviceSort")
include(":ClassHunter")
include(":DontResetIfBootedAndConnected")
include(":FreeNotifications")
include(":MotionEventMod")
include(":MuteSlf4jWarnings")
include(":OpenWifiOnTop")
include(":PersistentForegroundServiceNotifications")
include(":PreventAudioFocus")
include(":ResetAllNotificationChannels")
include(":RotationControl")
include(":reflection")
include(":logger")

include(":template")
