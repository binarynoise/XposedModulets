plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    namespace = "de.binarynoise.AutomaticAdvancedSettingsExpander"
    
    defaultConfig {
        minSdk = 16
        targetSdk = 33
    }
    
    signingConfigs {
        create("release") {
            keyAlias = "automaticadvancedsettingsexpander"
        }
    }
}
