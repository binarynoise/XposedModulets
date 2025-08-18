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
    implementation(libs.github.api)
    
    // Hack to make the libs accessor work
    // https://github.com/gradle/gradle/issues/15383
    compileOnly(files(libs::class.java.superclass.protectionDomain.codeSource.location))
}

gradlePlugin {
    plugins {
        register("commonAndroidApplicationPlugin") {
            id = "common.android.application"
            implementationClass = "CommonAndroidApplication"
        }
        register("commonAndroidLibraryPlugin") {
            id = "common.android.library"
            implementationClass = "CommonAndroidLibrary"
        }
        register("commonKotlinAndroidPlugin") {
            id = "common.kotlin.android"
            implementationClass = "CommonKotlinAndroid"
        }
        register("commonKotlinJvmPlugin") {
            id = "common.kotlin.jvm"
            implementationClass = "CommonKotlinJvm"
        }
        register("commonKotlinMultiplatformPlugin") {
            id = "common.kotlin.multiplatform"
            implementationClass = "CommonKotlinMultiplatform"
        }
        register("commonJvmPlugin") {
            id = "common.jvm"
            implementationClass = "CommonJvm"
        }
        register("commonGithubPlugin") {
            id = "common.github"
            implementationClass = "GithubPlugin"
        }
    }
}
