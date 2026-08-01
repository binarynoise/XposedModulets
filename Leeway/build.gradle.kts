plugins {
    alias(libs.plugins.buildlogic.android.application)
    alias(libs.plugins.buildlogic.kotlin.android)
}

android {
    namespace = "com.programminghoch10.leeway"
    
    defaultConfig {
        minSdk = 21
        targetSdk = 36
    }
}

dependencies {
    implementation(project(":logger"))
}
