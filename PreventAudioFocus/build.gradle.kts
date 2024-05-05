plugins {
    alias(libs.plugins.buildlogic.android.application)
}

android {
    namespace = "com.programminghoch10.PreventAudioFocus"
    
    defaultConfig {
        minSdk = 23
        targetSdk = 35
    }
}
