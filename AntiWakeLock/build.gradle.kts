plugins {
    alias(libs.plugins.buildlogic.android.application)
    alias(libs.plugins.buildlogic.kotlin.android)
}

android {
    namespace = "com.programminghoch10.AntiWakeLock"
    
    defaultConfig {
        minSdk = 1
        targetSdk = 36
    }
}
