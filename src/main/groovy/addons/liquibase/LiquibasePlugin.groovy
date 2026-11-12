/**
 * Copyright (c) 2023. Cellpoint Digital
 * www.cellpointdigital.com
 *
 */
package addons.liquibase

import addons.core.AddonsPlugin
import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.Delete

class LiquibasePlugin extends AddonsPlugin {

    @Override
    protected void applyTo(Project project) {
        project.tasks.register('dockerPrepare', Copy) { Copy task ->
            task.from("src/main/docker/Dockerfile")
            task.from("src/main/sh/") { it.into "app/scripts" }
            task.from("src/main/resources/") { it.into "app/resources" }
            task.into(project.layout.buildDirectory.dir(LiquibaseDockerTask.DOCKER_DIR).get())
        }

        project.tasks.register('clean', Delete) { Delete task ->
            task.delete(project.layout.buildDirectory)
        }

        project.tasks.register('docker', LiquibaseDockerTask) { LiquibaseDockerTask task ->
            task.dependsOn 'dockerPrepare'
        }
    }
}
