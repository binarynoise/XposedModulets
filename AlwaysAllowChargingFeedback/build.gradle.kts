plugins {
    alias(libs.plugins.buildlogic.android.application)
    alias(libs.plugins.buildlogic.kotlin.android)
}

android {
    namespace = "de.binarynoise.AlwaysAllowChargingFeedback"
    
    defaultConfig {
        minSdk = 28
        targetSdk = 36
    }
}

dependencies {}
