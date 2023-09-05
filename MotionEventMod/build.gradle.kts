plugins {
    id("com.android.application")
}

android {
    val packageName = "com.programminghoch10.MotionEventMod"
    namespace = packageName
    
    defaultConfig {
        applicationId = packageName
        minSdk = 14
        targetSdk = 33
    }
}
