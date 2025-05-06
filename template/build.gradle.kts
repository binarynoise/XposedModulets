plugins {
    alias(libs.plugins.buildlogic.android.application)
    alias(libs.plugins.buildlogic.kotlin.android)
}

android {
    namespace = "de.binarynoise.Template"
    
    defaultConfig {
        minSdk = 21
        targetSdk = 33
    }
}

dependencies {}
