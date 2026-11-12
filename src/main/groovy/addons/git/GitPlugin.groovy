/**
 * Copyright (c) 2023. Cellpoint Digital
 * www.cellpointdigital.com
 *
 */
package addons.git

import addons.core.AddonsPlugin
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.Task

class GitPlugin extends AddonsPlugin {

    @Override
    protected void applyTo(Project project) {
        def gitInfoProvider = project.providers.of(GitInfoValueSource.class) {}
        GitInfo gitInfo = gitInfoProvider.get()

        println "GIT"
        println "  branchName: ${gitInfo.branchName}"
        println "  commitId: ${gitInfo.commitId}"
        println "  tags: ${gitInfo.tags}"
        println "  closestTagName: ${gitInfo.closestTagName}"
        println "  dirty: ${gitInfo.dirty}"

        // expose this value globally
        project.ext.gitDetails = gitInfo

        project.tasks.register('updateAddons', Task) { Task task ->
            task.group = 'git'
            task.description = 'Updating the "addons" submodule to the latest commit in the "main" branch on the origin server.'
            task.doLast {
                [
                    'git -C addons fetch origin',
                    'git -C addons checkout -f -B main origin/main',
                    'git -C addons reset --hard origin/main',
                    'git -C addons clean -fd',
                    'git add addons'
                ].each { command ->
                    def process = command.execute()
                    process.waitForProcessOutput(System.out, System.err)
                    if (process.exitValue() != 0) {
                        throw new GradleException("Command failed (${process.exitValue()}): ${command}")
                    }
                }
            }
        }
    }
}
