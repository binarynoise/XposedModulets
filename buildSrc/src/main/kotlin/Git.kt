import org.gradle.api.Project
import org.gradle.process.ExecOutput

private fun Project.getCommitCountExec() = providers.exec {
    executable("git")
    args("rev-list", "--count", "HEAD", projectDir.absolutePath, rootProject.file("buildSrc").absolutePath)
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

fun Project.getCommitCount() = getCommitCountExec().standardOutput.asText.get().trim().toInt()

fun Project.getCommitHash() = getCommitHashExec().standardOutput.asText.get().trim()

fun Project.getWorkingTreeClean() = getWorkingTreeCleanExec().result.orNull?.exitValue == 0

fun Project.getAllCommitsPushed() = getAllCommitsPushedExec().result.orNull?.exitValue == 0
