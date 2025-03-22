plugins {
    alias(libs.plugins.buildlogic.android.application)
    kotlin("android")
}

android {
    namespace = "de.binarynoise.muteSlf4jWarnings"
    
    defaultConfig {
        targetSdk = 33
    }
}

dependencies {
    implementation(project(":reflection"))
}
