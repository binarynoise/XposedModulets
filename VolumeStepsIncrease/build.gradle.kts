plugins {
    alias(libs.plugins.buildlogic.android.application)
    alias(libs.plugins.buildlogic.kotlin.android)
}

android {
    namespace = "com.programminghoch10.VolumeStepsIncrease"
    
    defaultConfig {
        minSdk = 23
        targetSdk = 35
    }
}

dependencies {
    implementation(libs.androidx.preference.ktx)
}
