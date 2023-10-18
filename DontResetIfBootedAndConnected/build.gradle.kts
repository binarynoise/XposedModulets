plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    namespace = "de.binarynoise.dontResetIfBootedAndConnected"
    
    defaultConfig {
        minSdk = 30
        targetSdk = 33
    }
}

dependencies {

}
