plugins {
    alias(libs.plugins.buildlogic.android.application)
    alias(libs.plugins.buildlogic.kotlin.android)
}

android {
    namespace = "de.binarynoise.HideAbi"
    
    defaultConfig {
        minSdk = 21
        targetSdk = 36
        buildConfigField("String", "SHARED_PREFERENCES_NAME", "\"hide_abi\"")
    }
    
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(projects.reflection)
    implementation(projects.logger)
    implementation(libs.hiddenapibypass)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.preference.ktx)
}
