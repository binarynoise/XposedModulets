plugins {
    alias(libs.plugins.buildlogic.android.application)
    alias(libs.plugins.buildlogic.kotlin.android)
}

android {
    namespace = "com.programminghoch10.DisableSounds"
    
    defaultConfig {
        minSdk = 17
        targetSdk = 36
    }
}
