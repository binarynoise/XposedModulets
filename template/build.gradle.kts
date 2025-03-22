plugins {
    alias(libs.plugins.buildlogic.android.application)
    kotlin("android")
}

android {
    namespace = "de.binarynoise.Template"
    
    defaultConfig {
        minSdk = 21
        targetSdk = 33
    }
}

dependencies {

}
