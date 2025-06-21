plugins {
    alias(libs.plugins.buildlogic.android.application)
}

android {
    namespace = "com.programminghoch10.UpsideWifi"
    
    defaultConfig {
        minSdk = 21
        targetSdk = 35
    }
}
