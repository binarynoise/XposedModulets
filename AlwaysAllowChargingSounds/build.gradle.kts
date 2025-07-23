plugins {
    alias(libs.plugins.buildlogic.android.application)
    alias(libs.plugins.buildlogic.kotlin.android)
}

android {
    namespace = "de.binarynoise.AlwaysAllowChargingSounds"
    
    defaultConfig {
        minSdk = 28
        targetSdk = 36
    }
}

dependencies {}
