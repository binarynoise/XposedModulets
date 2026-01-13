plugins {
    alias(libs.plugins.buildlogic.android.application)
}

android {
    namespace = "com.programminghoch10.AntiBrightnessChange"
    
    defaultConfig {
        minSdk = 17
        targetSdk = 36
    }
}
