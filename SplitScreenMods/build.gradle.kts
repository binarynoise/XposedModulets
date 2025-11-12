plugins {
    alias(libs.plugins.buildlogic.android.application)
    alias(libs.plugins.buildlogic.kotlin.android)
}

android {
    namespace = "com.programminghoch10.SplitScreenMods"
    
    defaultConfig {
        minSdk = 33
        targetSdk = 36
    }
}

dependencies {
    implementation(project(":logger"))
}
