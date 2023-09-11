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
