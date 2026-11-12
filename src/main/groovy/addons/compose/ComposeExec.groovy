/**
 * Copyright (c) 2023. Cellpoint Digital
 * www.cellpointdigital.com
 *
 * Exposes tasks for working with local docker compose plugin (docker-compose)
 */
package addons.compose

import org.gradle.api.DefaultTask
import org.gradle.api.Task
import org.gradle.api.file.Directory
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations

import javax.inject.Inject

abstract class ComposeExec extends DefaultTask {

    @Inject
    abstract ExecOperations getExecOperations()

    @Internal
    String composeProjectName
    @Internal
    List<String> composeCommand
    @Internal
    String dockerComposeFile = 'docker-compose.yml'
    @Internal
    Directory runSubDirectory;

    private static List<String> dockerComposeCommand

    @Override
    Task configure(Closure closure) {
        composeProjectName = obtainComposeProjectName()
        runSubDirectory = project.rootProject.layout.projectDirectory.dir('run')
        return super.configure(closure)
    }
    
    @TaskAction
    void executeCompose() {
        def cmd = obtainDockerComposeCommand() + [ '-p', composeProjectName, '-f', dockerComposeFile] + composeCommand
        execOperations.exec {
            it.workingDir(runSubDirectory)
            it.commandLine(cmd)
        }
    }

    List<String> obtainDockerComposeCommand() {
        if (dockerComposeCommand == null) {
            int exitValue = execOperations.exec { it.commandLine('docker', 'compose', 'version') }.getExitValue()
            dockerComposeCommand = exitValue == 0 ? ['docker', 'compose'] : ['docker-compose']
        }
        return dockerComposeCommand;
    }

    // get name of the docker-compose project from ".env" file or from root project name.
    private String obtainComposeProjectName() {
        def envFile = project.rootProject.layout.projectDirectory.file("run/.env").getAsFile()
        if (envFile.canRead()) {
            for (String line : envFile.readLines()) {
                def (key, value) = line.tokenize('=')
                if (key != null && value != null && key.trim() == 'COMPOSE_PROJECT_NAME') {
                    return value
                }
            }
        }
        def name = project.rootProject.name // usually name is 'com.cellpointdigital.<project_name>'
        return name.substring(name.lastIndexOf('.') + 1)
    }

    void dependsOnAllExisted(String taskName) {
        def containers = project.rootProject.allprojects.tasks
        def existed = containers.flatten().findAll { it.name == taskName }
        println("Set '${name}' task to be depended on ${existed}")
        dependsOn(existed)
    }

    void dependsOnIfRequested(String... checked) {
        def taskNames = project.gradle.startParameter.taskNames
        checked.findAll { taskNames.contains(it) }.forEach { dependsOnAllExisted(it) }
    }
}
