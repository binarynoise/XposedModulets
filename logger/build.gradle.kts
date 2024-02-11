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
    implementation("androidx.collection:collection-ktx:1.4.0")
    implementation("androidx.core:core-ktx:1.12.0")
}
