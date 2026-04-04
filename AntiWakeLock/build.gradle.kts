plugins {
    alias(libs.plugins.buildlogic.android.application)
    alias(libs.plugins.buildlogic.kotlin.android)
}

android {
    namespace = "com.programminghoch10.AntiWakeLock"
    
    defaultConfig {
        minSdk = 14
        targetSdk = 36
    }
}

dependencies {
    implementation(project(":logger"))
}
