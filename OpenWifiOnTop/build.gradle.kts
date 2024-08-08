plugins {
    id("com.android.application")
    kotlin("android")
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
}
