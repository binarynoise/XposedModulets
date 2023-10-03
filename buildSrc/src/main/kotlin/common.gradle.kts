import java.io.FileInputStream
import java.util.*
import com.android.build.gradle.BaseExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.kohsuke.github.GHReleaseBuilder
import org.kohsuke.github.GitHub

val isAndroid = plugins.hasPlugin("com.android.application")
val isAndroidLib = plugins.hasPlugin("com.android.library")
val isJavaLib = plugins.hasPlugin("java-library")
val isKotlinLib = plugins.hasPlugin("org.jetbrains.kotlin.jvm")
val isKotlinAndroid = plugins.hasPlugin("org.jetbrains.kotlin.android")

if (isAndroid) println("android")
if (isAndroidLib) println("android lib")
if (isJavaLib) println("java lib")
if (isKotlinLib) println("kotlin lib")
if (isKotlinAndroid) println("kotlin android")

val javaVersion = JavaVersion.VERSION_17
val javaVersionInt = javaVersion.majorVersion.toInt()

val commitCount = getCommitCount()
val commitHash = getCommitHash()
val workingTreeClean = getWorkingTreeClean()
val allCommitsPushed = getAllCommitsPushed()

if (isAndroid || isAndroidLib) {
    val android = extensions.getByType<BaseExtension>()
    with(android) {
        
        val propsFile = rootProject.file("keystore.properties")
        if (propsFile.exists()) {
            val props = Properties()
            props.load(FileInputStream(propsFile))
            
            signingConfigs {
                maybeCreate("release").apply {
                    storeFile = storeFile ?: rootProject.file(props["storeFile"].toString())
                    
                    check(storeFile != null && storeFile!!.exists()) { "keystore does not exist" }
                    
                    storePassword = storePassword ?: props["storePassword"].toString()
                    keyAlias = keyAlias ?: props["keyAlias"].toString()
                    keyPassword = keyPassword ?: props["keyPassword"].toString()
                }
            }
            defaultConfig {
                signingConfig = signingConfigs.getByName("release")
            }
        } else {
            defaultConfig {
                signingConfig = signingConfigs.getByName("debug")
            }
        }
        
        defaultConfig {
            versionCode = commitCount
            versionName = "$commitCount${if(workingTreeClean) "-" else "+"}$commitHash"
            
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "../proguard-rules.pro")
            if (isAndroidLib) {
                consumerProguardFiles("consumer-rules.pro")
            }
        }
        
        buildTypes {
            getByName("release") {
                isMinifyEnabled = true
                multiDexEnabled = false
                
                if (isAndroid) {
                    isShrinkResources = true
                }
            }
            getByName("debug") {
                isMinifyEnabled = false
                isShrinkResources = false
                multiDexEnabled = false
                versionNameSuffix = "-dev"
            }
        }
        
        compileOptions {
            sourceCompatibility = javaVersion
            targetCompatibility = javaVersion
        }
    }
}

if (isAndroid) {
    val android = extensions.getByType<BaseExtension>()
    
    tasks.create("createGithubRelease") {
        val file = rootProject.file("local.properties")
        val properties = Properties()
        properties.load(file.inputStream())
        
        val repo = properties["github_repo"]
        check(repo != null && repo is String) { "github_repo not provided in local.properties " }
        val token = properties["github_api_key"]
        check(token != null && token is String) { "github_api_key not provided in local.properties" }
        
        if (workingTreeClean && allCommitsPushed) {
            dependsOn("assembleRelease")
        }
        
        doFirst action@{
            check(workingTreeClean) { "Commit all changes before creating release." }
            check(allCommitsPushed) { "Sync remote before creating release." }
            
            val packageRelease = project.tasks.getByName<DefaultTask>("packageRelease")
            
            val outputs = packageRelease.outputs.files
            val apks = outputs.filter { it.isDirectory }.flatMap { it.listFiles { file -> file.extension == "apk" }!!.toList() }
            
            val github = GitHub.connectUsingOAuth(token)
            val repository = github.getRepository(repo)
            
            val tagName = "${android.namespace}-v$commitCount"
            val name = "${project.name}-v$commitCount"
            
            if (repository.getReleaseByTagName(tagName) != null) {
                println("Release $name already exists")
                return@action
            }
            
            val release = repository.createRelease(tagName).name(name).draft(true).makeLatest(GHReleaseBuilder.MakeLatest.FALSE).create()
            
            apks.forEach {
                release.uploadAsset("${project.name}-v$commitCount.apk", it.inputStream(), "application/vnd.android.package-archive")
            }
            
            println("Created release ${release.name}: ${release.htmlUrl}")
        }
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = javaVersion.toString()
}

tasks.withType<JavaCompile> {
    sourceCompatibility = javaVersion.toString()
    targetCompatibility = javaVersion.toString()
}

if (isAndroid || isAndroidLib) {
    dependencies {
        add("compileOnly", "de.robv.android.xposed:api:82")
    }
}

if (isKotlinLib || isKotlinAndroid) {
    dependencies {
        add("implementation", "org.jetbrains:annotations:24.0.1")
    }
}

println("applied common on $project")
