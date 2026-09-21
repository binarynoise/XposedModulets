plugins {
    alias(libs.plugins.buildlogic.android.application)
    alias(libs.plugins.buildlogic.kotlin.android)
}

android {
    namespace = "com.programminghoch10.MotionEventMod"
    
    defaultConfig {
        minSdk = 14
        targetSdk = 37
        buildConfigField("String", "SHARED_PREFERENCES_NAME", "\"MotionEventMod\"")
        buildConfigField("String", "TAG", "\"${namespace!!.split(".").last()}\"")
    }
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.preference.ktx)
}
