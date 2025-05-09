plugins {
    alias(libs.plugins.buildlogic.android.application)
    alias(libs.plugins.buildlogic.kotlin.android)
}

android {
    namespace = "de.binarynoise.betterBluetoothDeviceSort"
    
    defaultConfig {
        minSdk = 30
        targetSdk = 33
    }
}

dependencies {
    implementation(projects.logger)
    implementation(projects.reflection)
}
