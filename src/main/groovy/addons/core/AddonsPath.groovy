/**
 * Copyright (c) 2023. Cellpoint Digital
 * www.cellpointdigital.com
 *
 */
package addons.core

import org.gradle.api.Project
import org.gradle.api.initialization.IncludedBuild

class AddonsPath {

    static String resolve(Project project) {
        def configured = project.findProperty('addonsPath')
        if (configured != null && !configured.toString().isBlank()) {
            return configured.toString()
        }

        IncludedBuild addonsBuild = project.gradle.includedBuilds.find { IncludedBuild build ->
            build.projectDir == new File(project.rootDir, 'addons')
        } ?: project.gradle.includedBuilds.find { IncludedBuild build ->
            build.projectDir.name == 'addons'
        }

        def addonsDir = addonsBuild?.projectDir ?: new File(project.rootDir, 'addons')
        return new File(addonsDir, 'src').absolutePath
    }

    static void configure(Project project) {
        if (!project.ext.has('addonsPath')) {
            project.ext.addonsPath = resolve(project)
        }
    }
}
