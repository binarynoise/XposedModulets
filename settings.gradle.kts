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
include(":DontResetIfBootedAndConnected")
include(":FreeNotifications")
include(":MotionEventMod")
include(":ResetAllNotificationChannels")
include(":reflection")
include(":logger")

include(":template")
