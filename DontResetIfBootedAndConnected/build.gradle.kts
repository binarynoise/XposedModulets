plugins {
    alias(libs.plugins.buildlogic.android.application)
    alias(libs.plugins.buildlogic.kotlin.android)
}

android {
    namespace = "de.binarynoise.dontResetIfBootedAndConnected"
    
    defaultConfig {
        minSdk = 30
        targetSdk = 33
    }
}

dependencies {}
