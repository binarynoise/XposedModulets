plugins {
    alias(libs.plugins.buildlogic.android.application)
    alias(libs.plugins.buildlogic.kotlin.android)
}

android {
    namespace = "com.programminghoch10.CodecMod"
    
    defaultConfig {
        minSdk = 16
        targetSdk = 37
        multiDexEnabled = true
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
    }
}

dependencies {
    implementation(libs.androidx.preference.ktx)
    coreLibraryDesugaring(libs.android.desugarJdkLibs)
}
