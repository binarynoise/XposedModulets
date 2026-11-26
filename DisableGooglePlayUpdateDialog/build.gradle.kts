plugins {
    alias(libs.plugins.buildlogic.android.application)
    alias(libs.plugins.buildlogic.kotlin.android)
}

android {
    namespace = "com.programminghoch10.DisableGooglePlayUpdateDialog"
    
    defaultConfig {
        targetSdk = 30
    }
}
