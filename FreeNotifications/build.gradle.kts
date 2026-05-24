plugins {
    alias(libs.plugins.buildlogic.android.application)
    alias(libs.plugins.buildlogic.kotlin.android)
}

android {
    namespace = "de.binarynoise.freeNotifications"
    
    defaultConfig {
        minSdk = 26
        targetSdk = 36
    }
}

dependencies {
    implementation(projects.reflection)
    implementation(projects.logger)
}
