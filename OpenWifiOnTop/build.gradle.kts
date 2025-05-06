plugins {
    alias(libs.plugins.buildlogic.android.application)
    alias(libs.plugins.buildlogic.kotlin.android)
}

android {
    namespace = "de.binarynoise.openWifiOnTop"
    
    defaultConfig {
        minSdk = 29
        targetSdk = 34
    }
}

dependencies {
    implementation(project(":reflection"))
    implementation(project(":logger"))
}
