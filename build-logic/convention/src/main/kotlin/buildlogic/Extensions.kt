package buildlogic

import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.Project
import org.gradle.kotlin.dsl.*

internal val Project.libs: LibrariesForLibs
    get() = extensions.getByType()
