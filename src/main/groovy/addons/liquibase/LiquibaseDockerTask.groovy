/**
 * Copyright (c) 2023. Cellpoint Digital
 * www.cellpointdigital.com
 *
 */
package addons.liquibase

import org.gradle.api.DefaultTask
import org.gradle.api.Task
import org.gradle.api.file.Directory
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations

import javax.inject.Inject

/**
 * Configure liquibase docker task with update checking
 */
abstract class LiquibaseDockerTask extends DefaultTask {

    public static final String DOCKER_DIR = 'docker'

    private String tagName
    private Directory dockerDir

    @Inject
    abstract ExecOperations getExecOperations()

    @TaskAction
    void executeDocker() {
        def cmd = ['docker', 'build', dockerDir.toString(), '-t', tagName]
        execOperations.exec({ it.commandLine cmd })
    }

    String obtainCurrentSha() {
        def output = new ByteArrayOutputStream()
        def cmd = ["docker", "images", "--no-trunc", "--quiet", tagName]
        execOperations.exec({
            it.commandLine cmd
            it.standardOutput = output
        })
        return output.toString("UTF-8")
    }

    Task configure(Closure closure) {
        tagName = "${project.rootProject.name}.${project.name}:latest"
        dockerDir = project.layout.buildDirectory.dir(DOCKER_DIR).get()

        def shaFile = project.layout.buildDirectory.file("caching/sha.txt").get().getAsFile()

        //BEGIN: before action analyzing if it needs to execute
        outputs.cacheIf { true }

        inputs.dir(dockerDir)
                .withPropertyName("docker")
                .withPathSensitivity(PathSensitivity.RELATIVE)

        def self = this;
        outputs.upToDateWhen {
            return shaFile.exists() && self.obtainCurrentSha() == shaFile.getText()
        }
        //END: before action analyzing if it needs to execute

        //AFTER: save cache state
        doLast {
            shaFile.parentFile.mkdirs()
            shaFile.write(self.obtainCurrentSha())
        }

        return super.configure(closure)
    }

}
