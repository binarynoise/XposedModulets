plugins {
    alias(libs.plugins.buildlogic.android.application)
    kotlin("android")
}

android {
    namespace = "de.binarynoise.freeNotifications"
    
    defaultConfig {
        minSdk = 26
        targetSdk = 33
    }
}

dependencies {
    implementation(project(":reflection"))
}
