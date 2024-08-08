plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    namespace = "de.binarynoise.ClassHunter"
    
    defaultConfig {
        minSdk = 26
        targetSdk = 33
        
        buildConfigField("String", "targetClass", """"..."""")
    }
    
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
}
