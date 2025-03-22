@file:Suppress("UnstableApiUsage")

buildscript {
    repositories {
        google {
            content {
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
                includeGroupAndSubgroups("androidx")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
//    alias(libs.plugins.application) apply false // noop, but throws exception
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    
    alias(libs.plugins.buildlogic.github)
}

allprojects {
    tasks.withType<Github> {
        configurationFile = rootProject.file("github.properties")
    }
}
