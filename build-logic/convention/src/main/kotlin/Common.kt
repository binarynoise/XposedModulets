@file:Suppress("unused")

import java.io.File
import java.io.FileInputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import buildlogic.git.getCommitCount
import buildlogic.git.getCommitHash
import buildlogic.git.getWorkingTreeClean
import buildlogic.libs
import com.android.build.api.dsl.ApplicationExtension
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.kotlin.dsl.*

class CommonAndroidApplication : Plugin<Project> {
    private val commonAndroid = CommonAndroid()
    
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.application")
            }
            
            commonAndroid.apply(target)
            
            extensions.configure<ApplicationExtension> {
                defaultConfig {
                    versionCode = getCommitCount()
                }
                
                setupSigning(rootProject.projectDir)
                setupVersioning(target)
                
                defaultConfig.proguardFiles(getDefaultProguardFile("proguard-android.txt"), "../proguard-rules.pro")
                
                buildTypes {
                    getByName("release") {
                        isMinifyEnabled = true
                        isShrinkResources = true
                    }
                }
                
                buildFeatures {
                    buildConfig = true
                }
                
                
                dependenciesInfo {
                    includeInApk = false
                    includeInBundle = false
                }
                
                packaging {
                    resources.excludes += listOf(
                        "**/*.kotlin_builtins",
                        "**/*.kotlin_metadata",
                        "**/*.kotlin_module",
                        "kotlin-tooling-metadata.json",
                    )
                }
                
            }
        }
    }
    
    fun ApplicationExtension.setupSigning(projectBaseDir: File) {
        val propsFile = projectBaseDir.resolve("keystore.properties")
        if (!propsFile.exists()) {
            defaultConfig {
                signingConfig = signingConfigs.getByName("debug")
            }
            return
        }
        
        val props = Properties()
        props.load(FileInputStream(propsFile))
        
        signingConfigs {
            maybeCreate("release").apply {
                storeFile = storeFile ?: projectBaseDir.resolve(props["storeFile"].toString())
                
                check(storeFile != null && storeFile!!.exists()) { "keystore does not exist" }
                
                storePassword = storePassword ?: props["storePassword"].toString()
                keyAlias = keyAlias ?: props["keyAlias"].toString()
                keyPassword = keyPassword ?: props["keyPassword"].toString()
                
                maybeCreate("debug").let { debug ->
                    debug.storeFile = storeFile
                    debug.storePassword = storePassword
                    debug.keyAlias = keyAlias
                    debug.keyPassword = keyPassword
                }
            }
        }
        defaultConfig {
            signingConfig = signingConfigs.getByName("release")
        }
    }
    
    fun ApplicationExtension.setupVersioning(project: Project) {
        val commitCount = project.getCommitCount()
        val commitHash = project.getCommitHash()
        val workingTreeClean = project.getWorkingTreeClean()
        val date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
        
        defaultConfig {
            versionCode = commitCount
            versionName = "$commitCount${if (workingTreeClean) "-" else "+"}$commitHash-$date"
        }
    }
}

class CommonAndroidLibrary : Plugin<Project> {
    private val commonAndroid = CommonAndroid()
    
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.library")
            }
            
            commonAndroid.apply(target)
            
            extensions.configure<LibraryExtension> {
                
                defaultConfig.consumerProguardFiles("consumer-rules.pro")
                
                buildTypes {
                    getByName("release") {
                        isMinifyEnabled = false
                        isShrinkResources = false
                        
                    }
                }
            }
            
        }
    }
}

private class CommonAndroid : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            extensions.configure<BaseExtension> {
                compileSdkVersion(36)
                
                dependencies {
                    add("implementation", libs.androidx.annotation)
                    add("compileOnly", libs.xposed.api)
                }
                
                buildTypes {
                    
                    getByName("debug") {
                        isMinifyEnabled = false
                        isShrinkResources = false
                        versionNameSuffix = "-dev"
                    }
                }
                
                lintOptions {
                    disable += "DiscouragedApi"
                    disable += "ExpiredTargetSdkVersion"
                    disable += "MissingApplicationIcon"
                    disable += "MissingPermission"
                    disable += "OldTargetApi"
                    disable += "PrivateApi"
                    disable += "UnusedAttribute"
                }
                
                compileOptions {
                    val jvmVersion = target.extensions.getByType<JavaPluginExtension>().sourceCompatibility
                    sourceCompatibility = jvmVersion
                    targetCompatibility = jvmVersion
                }
            }
        }
    }
}

class CommonKotlinAndroid : Plugin<Project> {
    private val commonKotlin = CommonKotlin()
    
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("org.jetbrains.kotlin.android")
            }
            
            commonKotlin.apply(target)
        }
    }
}

class CommonKotlinJvm : Plugin<Project> {
    private val commonKotlin = CommonKotlin()
    
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("org.jetbrains.kotlin.jvm")
            }
            
            commonKotlin.apply(target)
        }
    }
}

class CommonKotlinMultiplatform : Plugin<Project> {
    private val commonKotlin = CommonKotlin()
    
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("org.jetbrains.kotlin.multiplatform")
            }
            
            commonKotlin.apply(target)
        }
    }
}

private class CommonKotlin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                
            }
            
            dependencies {
                add("implementation", libs.kotlin.bom)
                add("implementation", libs.jebtrains.annotations)
            }
        }
    }
}

class CommonJvm : Plugin<Project> {
    override fun apply(target: Project) {
    }
}
