plugins {
    alias(libs.plugins.buildlogic.android.application)
    alias(libs.plugins.buildlogic.kotlin.android)
}

android {
    namespace = "com.programminghoch10.EnableDataSaverTethering"
    
    defaultConfig {
        minSdk = 36
        targetSdk = 36
    }
}

dependencies {
    implementation(projects.reflection)
}
