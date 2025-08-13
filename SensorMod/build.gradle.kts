plugins {
    alias(libs.plugins.buildlogic.android.application)
}

android {
    namespace = "com.programminghoch10.SensorMod"
    
    defaultConfig {
        minSdk = 34
        targetSdk = 35
    }
}

dependencies {
    implementation(libs.androidx.preference)
}
