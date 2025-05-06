@file:Suppress("unused")

import java.util.*
import buildlogic.git.getAllCommitsPushed
import buildlogic.git.getCommitCount
import buildlogic.git.getWorkingTreeClean
import com.android.build.api.dsl.ApplicationExtension
import org.gradle.BuildAdapter
import org.gradle.BuildResult
import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Transformer
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.*
import org.gradle.work.DisableCachingByDefault
import org.kohsuke.github.GHReleaseBuilder
import org.kohsuke.github.GitHub

abstract class GithubPlugin : Plugin<Project> {
    
    override fun apply(target: Project) {
        with(target) {
            tasks.register<GithubClearReleaseTask>("githubClearReleases")
            subprojects {
                afterEvaluate {
                    if (extensions.findByType<ApplicationExtension>() != null) {
                        tasks.register<GithubCreateReleaseTask>("githubCreateRelease")
                    }
                }
            }
            
        }
    }
}

@DisableCachingByDefault
abstract class GithubCreateReleaseTask : Github() {
    
    private val debug = false
    
    @Input
    val commitCount = project.getCommitCount()
    
    @Input
    val workingTreeClean = project.getWorkingTreeClean()
    
    @Input
    val allCommitsPushed = project.getAllCommitsPushed()
    
    init {
        if (debug || (workingTreeClean && allCommitsPushed)) {
            dependsOn("packageRelease")
        }
    }
    
    @TaskAction
    fun createRelease() {
        val android = project.extensions.getByType<ApplicationExtension>()
        
        if (!debug) {
            check(workingTreeClean) { "Commit all changes before creating release" }
            check(allCommitsPushed) { "Push to remote before creating release" }
        }
        
        val github = GitHub.connectUsingOAuth(token.get())
        val repository = github.getRepository(repo.get())
        
        val tagName = "${android.namespace}-v$commitCount"
        val name = "${project.name}-v$commitCount"
        
        if (repository.getReleaseByTagName(tagName) != null) {
            doLast {
                project.logger.warn("Release $name already exists")
            }
            return
        }
        
        val release = repository.createRelease(tagName).name(name).draft(true).makeLatest(GHReleaseBuilder.MakeLatest.FALSE).create()
        
        val packageRelease = project.tasks.getByName("packageRelease")
        val outputs = packageRelease.outputs.files
        val apks = outputs.filter { it.isDirectory }.flatMap { it.listFiles { file -> file.extension == "apk" }.asList() }
        apks.forEach {
            release.uploadAsset("${project.name}-v$commitCount.apk", it.inputStream(), "application/vnd.android.package-archive")
        }
        
        val logString = "Created release ${release.name}: ${release.htmlUrl}"
        
        project.gradle.addBuildListener(object : BuildAdapter() {
            override fun buildFinished(result: BuildResult) {
                println(logString)
            }
        })
    }
}

@DisableCachingByDefault
abstract class GithubClearReleaseTask : Github() {
    
    @TaskAction
    fun clearReleases() {
        val github = GitHub.connectUsingOAuth(token.get())
        val repository = github.getRepository(repo.get())
        
        repository.listReleases().filter { it.isDraft }.forEach { release ->
            val name = release.name
            release.delete()
            println("Deleted release $name")
        }
    }
}

abstract class Github : DefaultTask() {
    
    init {
        notCompatibleWithConfigurationCache("needs to talk to GitHub")
    }
    
    @get:InputFile
    abstract val configurationFile: RegularFileProperty
    
    @Input
    val properties: Provider<Properties> = configurationFile.map { Properties().apply { load(it.asFile.inputStream()) } }
    
    @Input
    val repo: Provider<String?> = properties.map<String?>(PropertyFileTransformer("github_repo"))
    
    @Input
    val token: Provider<String> = properties.map<String?>(PropertyFileTransformer("github_api_key"))
    
    class PropertyFileTransformer(val key: String) : Transformer<String?, Properties> {
        override fun transform(`in`: Properties): String? {
            return `in`[key]?.toString()
        }
    }
}
