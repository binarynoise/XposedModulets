plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    val packageName = "de.binarynoise.resetAllNotificationChannels"
    namespace = packageName
    
    defaultConfig {
        applicationId = packageName
        minSdk = 21
        targetSdk = 33
    }
}

dependencies {

}
