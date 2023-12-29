import java.util.*
import org.kohsuke.github.GitHub

plugins {
    idea
}

idea {
    module {
        isDownloadSources = true
        isDownloadJavadoc = true
    }
}

allprojects {
    apply {
        plugin("idea")
    }
    
    idea {
        module {
            isDownloadSources = true
            isDownloadJavadoc = true
        }
    }
    
    // apply common in afterEvaluate because other plugins need to be loaded first
    afterEvaluate {
        apply {
            plugin("common")
        }
    }
}

tasks.create("clearAllDraftReleases") {
    val file = rootProject.file("local.properties")
    val properties = Properties()
    properties.load(file.inputStream())
    
    val repo = properties["github_repo"]
    val token = properties["github_api_key"]
    
    doFirst {
        check(repo != null && repo is String) { "github_repo not provided in local.properties" }
        check(token != null && token is String) { "github_api_key not provided in local.properties" }
        
        val github = GitHub.connectUsingOAuth(token)
        
        github.getRepository(repo).listReleases().filter { it.isDraft }.forEach { release ->
            val name = release.name
            release.delete()
            println("Deleted release $name")
        }
    }
}
