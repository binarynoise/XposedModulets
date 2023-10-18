plugins {
    id("com.android.library")
    kotlin("android")
}

android {
    namespace = "de.binarynoise.logger"
    
    defaultConfig {
        minSdk = 24
    }
}

dependencies {
    implementation("androidx.collection:collection-ktx:1.2.0")
    implementation("androidx.core:core-ktx:1.10.1")
}
