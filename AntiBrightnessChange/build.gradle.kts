plugins {
    alias(libs.plugins.buildlogic.android.application)
}

android {
    namespace = "com.programminghoch10.AntiBrightnessChange"
    
    defaultConfig {
        minSdk = 8
        targetSdk = 36
    }
}
