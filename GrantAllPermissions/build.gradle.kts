plugins {
    alias(libs.plugins.buildlogic.android.application)
    alias(libs.plugins.buildlogic.kotlin.android)
}

android {
    namespace = "com.programminghoch10.GrantAllPermissions"
    
    defaultConfig {
        minSdk = 21
        targetSdk = 35
    }
}
