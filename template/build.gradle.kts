plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    val packageName = "de.binarynoise.Template"
    namespace = packageName
    
    defaultConfig {
        applicationId = packageName
        minSdk = 21
        targetSdk = 33
        compileSdk = 33
        versionCode = 1
        versionName = "1.0"
    }
}

dependencies {

}