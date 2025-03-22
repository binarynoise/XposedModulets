plugins {
    alias(libs.plugins.buildlogic.android.application)
    kotlin("android")
}

android {
    namespace = "de.binarynoise.AutomaticAdvancedSettingsExpander"
    
    defaultConfig {
        minSdk = 16
        targetSdk = 33
    }
    
    signingConfigs {
        maybeCreate("release").apply {
            keyAlias = "automaticadvancedsettingsexpander"
        }
    }
}
