plugins {
    alias(libs.plugins.buildlogic.android.application)
}

android {
    val packageName = "com.programminghoch10.RotationControl"
    namespace = packageName
    
    defaultConfig {
        applicationId = packageName
        minSdk = 24
        targetSdk = 33
    }
}
