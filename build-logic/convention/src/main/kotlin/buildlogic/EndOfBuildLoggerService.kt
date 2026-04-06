package buildlogic

import buildlogic.EndOfBuildLoggerService.Companion.buildLogger
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.services.BuildService
import org.gradle.api.services.BuildServiceParameters

/**
 * A Gradle build service that accumulates messages during the build and prints them all at the end.
 *
 * Use the [Project.buildLogger] or [Task.buildLogger] extension properties to access the service.
 */
abstract class EndOfBuildLoggerService : BuildService<BuildServiceParameters.None>, AutoCloseable {
    private val messages = mutableListOf<String>()
    
    /**
     * Adds a message to be printed at the end of the build.
     *
     * @param message The message to log
     */
    @Synchronized
    fun addMessage(message: String) {
        messages.add(message)
    }
    
    /**
     * Prints all accumulated messages and clears the message list.
     * Called automatically by Gradle when the build completes.
     */
    @Synchronized
    override fun close() {
        messages.forEach { println(it) }
        messages.clear()
    }
    
    companion object {
        /**
         * Extension property to access the [EndOfBuildLoggerService] from a [Task].
         */
        val Task.buildLogger: EndOfBuildLoggerService
            get() = project.buildLogger
        
        /**
         * Extension property to access the [EndOfBuildLoggerService] from a [Project].
         */
        val Project.buildLogger: EndOfBuildLoggerService
            get() = gradle.sharedServices.registerIfAbsent(EndOfBuildLoggerService::class.java.name, EndOfBuildLoggerService::class.java).get()
    }
}
