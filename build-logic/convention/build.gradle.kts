import org.jetbrains.kotlin.gradle.dsl.JvmTarget

group = "buildlogic"

plugins {
    `kotlin-dsl`
}

val javaVersion = JavaVersion.VERSION_17

java {
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.fromTarget(javaVersion.toString())
    }
}

tasks {
    validatePlugins {
        enableStricterValidation = true
        failOnWarning = true
    }
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.android.tools.common)
    compileOnly(libs.kotlin.gradlePlugin)
//    compileOnly(libs.ksp.gradlePlugin)
    implementation(libs.github.api)
    
    // Hack to make the libs accessor work
    // https://github.com/gradle/gradle/issues/15383
    compileOnly(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
}

gradlePlugin {
    plugins {
        create("commonAndroidApplicationPlugin") {
            id = "common.android.application"
            implementationClass = "CommonAndroidApplication"
        }
        create("commonAndroidLibraryPlugin") {
            id = "common.android.library"
            implementationClass = "CommonAndroidLibrary"
        }
        create("commonKotlinAndroidPlugin") {
            id = "common.kotlin.android"
            implementationClass = "CommonKotlinAndroid"
        }
        create("commonKotlinJvmPlugin") {
            id = "common.kotlin.jvm"
            implementationClass = "CommonKotlinJvm"
        }
        create("commonKotlinMultiplatformPlugin") {
            id = "common.kotlin.multiplatform"
            implementationClass = "CommonKotlinMultiplatform"
        }
        create("commonJvmPlugin") {
            id = "common.jvm"
            implementationClass = "CommonJvm"
        }
    }
}
