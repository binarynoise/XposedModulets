plugins {
    alias(libs.plugins.buildlogic.android.application)
    alias(libs.plugins.buildlogic.kotlin.android)
}

android {
    val packageName = "com.programminghoch10.MotionEventMod"
    namespace = packageName
    
    defaultConfig {
        applicationId = packageName
        minSdk = 14
        targetSdk = 33
    }
    buildFeatures {
        buildConfig = true
    }
}
