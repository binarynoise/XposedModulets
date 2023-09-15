plugins {
    id("com.android.application")
}

android {
    val packageName = "com.programminghoch10.AntiBrightnessChange"
    namespace = packageName
    
    defaultConfig {
        applicationId = packageName
        minSdk = 33
        targetSdk = 33
        compileSdk = 33
        versionCode = 1
        versionName = "1.0"
    }
}

dependencies {}
