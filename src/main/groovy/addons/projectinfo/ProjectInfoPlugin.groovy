/**
 * Copyright (c) 2023. Cellpoint Digital
 * www.cellpointdigital.com
 *
 */
package addons.projectinfo

import addons.core.AddonsPlugin
import org.gradle.api.Project

class ProjectInfoPlugin extends AddonsPlugin {

    @Override
    protected void applyTo(Project project) {
        println "Project: ${project.name}"
        println "  Version: ${project.version}"
        println "  ProjectDir: ${project.projectDir}"
    }
}
