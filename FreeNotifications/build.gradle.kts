plugins {
    alias(libs.plugins.buildlogic.android.application)
    alias(libs.plugins.buildlogic.kotlin.android)
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
    implementation(project(":logger"))
}
