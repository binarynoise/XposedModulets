plugins {
    alias(libs.plugins.buildlogic.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    val packageName = "com.programminghoch10.RotationControl"
    namespace = packageName
    
    defaultConfig {
        applicationId = packageName
        minSdk = 24
        targetSdk = 36
    }
}

dependencies {
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.preference.ktx)
}
