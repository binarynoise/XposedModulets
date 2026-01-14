plugins {
    alias(libs.plugins.buildlogic.android.library)
    alias(libs.plugins.buildlogic.kotlin.android)
}

android {
    namespace = "de.binarynoise.logger"
    
    defaultConfig {
        minSdk = 1
    }
}

dependencies {
    debugCompileOnly(libs.androidx.collection.ktx)
    debugCompileOnly(libs.androidx.core.ktx)
}
