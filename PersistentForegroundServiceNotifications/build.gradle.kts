plugins {
    alias(libs.plugins.buildlogic.android.application)
    alias(libs.plugins.buildlogic.kotlin.android)
}

android {
    namespace = "de.binarynoise.persistentForegroundServiceNotifications"
    
    defaultConfig {
        minSdk = 34
        targetSdk = 34
    }
}

dependencies {
    implementation(projects.logger)
    implementation(projects.reflection)
}
