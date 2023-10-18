plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    namespace = "de.binarynoise.betterVerboseWiFiLogging"
    
    defaultConfig {
        minSdk = 30
        targetSdk = 33
    }
}

dependencies {
    implementation(project(":reflection"))
}
