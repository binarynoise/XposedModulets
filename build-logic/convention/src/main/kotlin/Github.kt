import buildlogic.git.getAllCommitsPushed
import buildlogic.git.getCommitCount
import buildlogic.git.getWorkingTreeClean
import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.*
import org.gradle.work.DisableCachingByDefault
import org.kohsuke.github.GHReleaseBuilder
import org.kohsuke.github.GitHub

@DisableCachingByDefault
abstract class GithubCreateReleaseTask : DefaultTask() {
    
    init {
        notCompatibleWithConfigurationCache("needs to talk to GitHub")
    }
    
    @get:Input
    abstract val repo: String
    
    @get:Input
    abstract val token: String
    
    @TaskAction
    fun createRelease() {
        val commitCount = project.getCommitCount()
        val workingTreeClean = project.getWorkingTreeClean()
        val allCommitsPushed = project.getAllCommitsPushed()
        
        val android = project.extensions.getByType<ApplicationExtension>()
        
        if (workingTreeClean && allCommitsPushed) {
            dependsOn("assembleRelease")
        }
        
        check(workingTreeClean) { "Commit all changes before creating release" }
        check(allCommitsPushed) { "Push to remote before creating release" }
        
        val packageRelease = project.tasks.getByName("packageRelease")
        
        val outputs = packageRelease.outputs.files
        val apks = outputs.filter { it.isDirectory }.flatMap { it.listFiles { file -> file.extension == "apk" }!!.toList() }
        
        val github = GitHub.connectUsingOAuth(token)
        val repository = github.getRepository(repo)
        
        val tagName = "${android.namespace}-v$commitCount"
        val name = "${project.name}-v$commitCount"
        
        if (repository.getReleaseByTagName(tagName) != null) {
            doLast {
                project.logger.warn("Release $name already exists")
            }
            return
        }
        
        val release = repository.createRelease(tagName).name(name).draft(true).makeLatest(GHReleaseBuilder.MakeLatest.FALSE).create()
        
        apks.forEach {
            release.uploadAsset("${project.name}-v$commitCount.apk", it.inputStream(), "application/vnd.android.package-archive")
        }
        
        doLast {
            project.logger.info("Created release ${release.name}: ${release.htmlUrl}")
        }
    }
}

@DisableCachingByDefault
abstract class GithubClearReleaseTask : DefaultTask() {
    
    init {
        notCompatibleWithConfigurationCache("needs to talk to GitHub")
    }
    
    @get:Input
    abstract val repo: String
    
    @get:Input
    abstract val token: String
    
    @TaskAction
    fun clearReleases() {
        
        val github = GitHub.connectUsingOAuth(token)
        val repository = github.getRepository(repo)
        
        repository.listReleases().filter { it.isDraft }.forEach { release ->
            val name = release.name
            release.delete()
            project.logger.info("Deleted release $name")
        }
    }
}
