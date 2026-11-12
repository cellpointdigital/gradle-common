/**
 * Copyright (c) 2023. Cellpoint Digital
 * www.cellpointdigital.com
 *
 * Exposes tasks for working with local docker compose plugin (docker-compose)
 */
package addons.compose

import addons.core.AddonsPlugin
import org.gradle.api.Project

class ComposePlugin extends AddonsPlugin {

    @Override
    protected void applyTo(Project project) {
        project.tasks.register('up', ComposeExec) { ComposeExec task ->
            task.group = 'docker-compose'
            task.description = 'Builds docker containers and starts up it in docker-compose project.'
            task.dependsOnIfRequested('clean', 'down')
            task.dependsOnAllExisted('docker')
            task.composeCommand = ['up', '-d']
        }

        project.tasks.register('down', ComposeExec) { ComposeExec task ->
            task.group = 'docker-compose'
            task.description = 'Stops docker-compose project.'
            task.composeCommand = ['down']
        }

        project.tasks.register('restart', ComposeExec) { ComposeExec task ->
            task.group = 'docker-compose'
            task.description = 'Just restarts docker-compose project.'
            task.dependsOn('down')
            task.dependsOnIfRequested('clean', 'build', 'docker')
            task.composeCommand = ['up', '-d']
        }
    }
}
