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
    debugImplementation("androidx.collection:collection-ktx:1.4.0")
    debugImplementation("androidx.core:core-ktx:1.12.0")
}
