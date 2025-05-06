plugins {
    alias(libs.plugins.buildlogic.android.application)
    alias(libs.plugins.buildlogic.kotlin.android)
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
