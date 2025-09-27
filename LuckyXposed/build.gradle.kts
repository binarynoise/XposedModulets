plugins {
    alias(libs.plugins.buildlogic.android.application)
    alias(libs.plugins.buildlogic.kotlin.android)
}

android {
    namespace = "com.programminghoch10.LuckyXposed"
    
    defaultConfig {
        minSdk = 33
        targetSdk = 35
    }
}

//noinspection UseTomlInstead
dependencies {
    val billingVersion = "8.0.0"
    compileOnly("com.android.billingclient:billing:$billingVersion")
    //compileOnly("com.android.billingclient:billing-ktx:${billingVersion}")
}
