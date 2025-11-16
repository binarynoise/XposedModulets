plugins {
    alias(libs.plugins.buildlogic.android.application)
    alias(libs.plugins.buildlogic.kotlin.android)
}

android {
    namespace = "com.programminghoch10.SplitScreenMods"
    
    defaultConfig {
        minSdk = 33
        targetSdk = 36
        buildConfigField("String", "SHARED_PREFERENCES_NAME", "\"splitscreen\"")
    }
}

dependencies {
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.preference.ktx)
    implementation(project(":logger"))
    compileOnly(libs.androidx.performance.unsafe)
}
