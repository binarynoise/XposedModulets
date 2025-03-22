plugins {
    alias(libs.plugins.buildlogic.android.application)
    kotlin("android")
}

android {
    namespace = "de.binarynoise.persistentForegroundServiceNotifications"
    
    defaultConfig {
        minSdk = 34
        targetSdk = 34
    }
}

dependencies {
    implementation(project(":logger"))
    implementation(project(":reflection"))
}
