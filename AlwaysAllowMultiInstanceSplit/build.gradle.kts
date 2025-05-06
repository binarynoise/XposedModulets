plugins {
    alias(libs.plugins.buildlogic.android.application)
    alias(libs.plugins.buildlogic.kotlin.android)
}

android {
    namespace = "de.binarynoise.AlwaysAllowMultiInstanceSplit"
    
    defaultConfig {
        minSdk = 35
        targetSdk = 35
    }
}

dependencies {
    implementation(project(":reflection"))
    implementation(project(":logger"))
}
