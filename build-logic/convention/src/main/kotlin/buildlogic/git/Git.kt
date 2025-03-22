@file:Suppress("UnstableApiUsage")

package buildlogic.git

import com.android.build.gradle.BaseExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.*
import org.gradle.process.ExecOutput

private fun Project.getCommitCountExec() = providers.exec {
    executable("git")
    args("rev-list", "--count", "HEAD")
    args(projectDir.absolutePath)
    args(rootProject.file("gradle").absolutePath)
    
    rootProject.projectDir.listFiles { file -> file.isFile && file.extension != "md" }!!.forEach { args(it.absolutePath) }
    
    extensions.findByType<BaseExtension>()?.let {
        val metadata = rootProject.file("metadata").resolve(it.namespace ?: "-")
        if (metadata.exists()) {
            args(metadata)
        }
    }
}

private fun Project.getCommitHashExec() = providers.exec {
    executable("git")
    args("rev-parse", "--short", "HEAD")
}

private fun Project.getWorkingTreeCleanExec() = providers.exec {
    executable("git")
    args("diff", "--quiet", "--exit-code", rootDir.absolutePath)
    isIgnoreExitValue = true
}

private fun Project.getAllCommitsPushedExec(): ExecOutput {
    providers.exec {
        executable("git")
        args("fetch")
        isIgnoreExitValue = true
    }
    
    return providers.exec {
        executable("git")
        args("diff", "--quiet", "--exit-code", "origin/main..main")
        isIgnoreExitValue = true
    }
}

fun Project.getCommitCount() = try {
    getCommitCountExec().standardOutput.asText.get().trim().toInt()
} catch (e: Exception) {
    logger.error("Failed to get commit count", e)
    0
}

fun Project.getCommitHash() = try {
    getCommitHashExec().standardOutput.asText.get().trim()
} catch (e: Exception) {
    logger.error("Failed to get commit hash", e)
    ""
}

fun Project.getWorkingTreeClean() = getWorkingTreeCleanExec().result.orNull?.exitValue == 0

fun Project.getAllCommitsPushed() = getAllCommitsPushedExec().result.orNull?.exitValue == 0
