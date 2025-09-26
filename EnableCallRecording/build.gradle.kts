plugins {
    alias(libs.plugins.buildlogic.android.application)
}

android {
    namespace = "com.programminghoch10.EnableCallRecord"
    
    defaultConfig {
        minSdk = 27
        targetSdk = 35
    }
}
