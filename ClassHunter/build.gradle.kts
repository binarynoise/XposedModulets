plugins {
    alias(libs.plugins.buildlogic.android.application)
    kotlin("android")
}

android {
    namespace = "de.binarynoise.ClassHunter"
    
    defaultConfig {
        minSdk = 26
        targetSdk = 33
        
        val file = File(projectDir, "targetClass.txt")
        if (!file.exists()) {
            file.createNewFile()
        }
        val classes = file.readLines().filterNot { it.startsWith("#") || it.isBlank() }
        buildConfigField("String[]", "targetClass", classes.joinToString("\", \"", "{\"", "\"}"))
        versionNameSuffix = classes.joinToString(", ", " (", ")")
    }
    
    buildFeatures {
        buildConfig = true
    }
}

dependencies {}
