/**
 * Copyright (c) 2023. Cellpoint Digital
 * www.cellpointdigital.com
 */
package addons.git

import org.gradle.api.provider.ValueSource
import org.gradle.api.provider.ValueSourceParameters
import org.gradle.process.ExecOperations

import javax.inject.Inject

// works as a source of some value
abstract class GitInfoValueSource implements ValueSource<GitInfo, ValueSourceParameters.None> {

    private static final String DIRTY_MARK = "**DIRTY**"
    private GitInfo info

    // most likely, this function is resolved by `providers.of(GitInfoValueSource.class)`
    @Inject
    abstract ExecOperations getExecOperations()

    private String run(String... command) {
        final ByteArrayOutputStream output = new ByteArrayOutputStream()
        execOperations.exec {
            it.commandLine command
            it.standardOutput = output
            it.ignoreExitValue = true
        }
        return (new String(output.toByteArray())).trim()
    }

    GitInfo obtain() {
        if (info == null) {
            String latestTag = run("git", "describe", "--tags", "--abbrev=0", "--dirty="+DIRTY_MARK)
            List<String> tags = run("git", "tag", "--contains", "HEAD").tokenize('\n')
            info = new GitInfo(
                    run("git", "rev-parse", "--abbrev-ref", "HEAD"),
                    run("git", "rev-parse", "HEAD"),
                    latestTag.replace(DIRTY_MARK, ""),
                    latestTag.contains(DIRTY_MARK),
                    tags
            )
        }
        return info
    }
}
