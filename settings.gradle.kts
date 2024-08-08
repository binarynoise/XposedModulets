@file:Suppress("UnstableApiUsage")

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
        maven("https://api.xposed.info/")
    }
}

rootProject.name = "XposedModulets"
include(":AntiBrightnessChange")
include(":AutomaticAdvancedSettingsExpander")
include(":BetterVerboseWiFiLogging")
include(":BetterBluetoothDeviceSort")
include(":ClassHunter")
include(":DontResetIfBootedAndConnected")
include(":FreeNotifications")
include(":MotionEventMod")
include(":MuteSlf4jWarnings")
include(":OpenWifiOnTop")
include(":PersistentForegroundServiceNotifications")
include(":ResetAllNotificationChannels")
include(":RotationControl")
include(":reflection")
include(":logger")

include(":template")
