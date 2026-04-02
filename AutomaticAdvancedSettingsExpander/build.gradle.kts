plugins {
    alias(libs.plugins.buildlogic.android.application)
    alias(libs.plugins.buildlogic.kotlin.android)
}

android {
    namespace = "de.binarynoise.AutomaticAdvancedSettingsExpander"
    
    defaultConfig {
        minSdk = 16
        targetSdk = 36
        
        buildConfigField("String", "SHARED_PREFERENCES_NAME", "\"automatic_advanced_settings_expander\"")
    }
    
    buildFeatures {
        buildConfig = true
    }
    
    signingConfigs {
        maybeCreate("release").apply {
            keyAlias = "automaticadvancedsettingsexpander"
        }
    }
}

dependencies {
    implementation(projects.logger)
//    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.preference.ktx)
}
