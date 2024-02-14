plugins {
    id("com.android.library")
    kotlin("android")
}

android {
    namespace = "de.binarynoise.logger"
    
    defaultConfig {
        minSdk = 19
    }
}

dependencies {
    debugImplementation("androidx.collection:collection:1.4.0")
}
