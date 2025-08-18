plugins {
    alias(libs.plugins.buildlogic.android.application)
    alias(libs.plugins.buildlogic.kotlin.android)
}

android {
    namespace = "com.programminghoch10.KeepSplitScreenRatio"
    
    defaultConfig {
        minSdk = 34
        targetSdk = 36
    }
}
