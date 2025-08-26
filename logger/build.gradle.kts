plugins {
    alias(libs.plugins.buildlogic.android.library)
    alias(libs.plugins.buildlogic.kotlin.android)
}

android {
    namespace = "de.binarynoise.logger"
    
    defaultConfig {
        minSdk = 19
    }
}

dependencies {
    debugImplementation(libs.androidx.collection.ktx)
    debugImplementation(libs.androidx.core.ktx)
}
