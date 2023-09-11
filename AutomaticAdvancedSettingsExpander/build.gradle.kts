plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    val packageName = "de.binarynoise.AutomaticAdvancedSettingsExpander"
    namespace = packageName
    
    defaultConfig {
        applicationId = packageName
        minSdk = 16
        targetSdk = 33
        compileSdk = 33
        versionCode = 2
        versionName = "1.1"
    }
    
    signingConfigs {
        create("release") {
            keyAlias = "automaticadvancedsettingsexpander"
        }
    }
}
