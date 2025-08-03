plugins {
    alias(libs.plugins.buildlogic.android.application)
}

android {
    namespace = "com.programminghoch10.CodecMod"
    
    defaultConfig {
        minSdk = 16
        targetSdk = 35
    }
}
dependencies {
    implementation(libs.androidx.preference)
}
