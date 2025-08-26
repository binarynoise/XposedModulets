plugins {
    alias(libs.plugins.buildlogic.android.application)
    alias(libs.plugins.buildlogic.kotlin.android)
}

android {
    namespace = "com.programminghoch10.DisableSounds"
    
    defaultConfig {
        minSdk = 17
        targetSdk = 36
        buildConfigField("String", "SHARED_PREFERENCES_NAME", "\"disable_sounds\"")
    }
}

dependencies {
    // fragment-ktx is included as transitive dependency through preference-ktx
    // the transitive dependency is a lower version though, which allows minSdk 17,
    // while explicit mention with the latest version forced minSdk 21
    //implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.preference.ktx)
    implementation(libs.kotlinx.coroutines.guava)
}
