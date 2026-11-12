/**
 * Copyright (c) 2023. Cellpoint Digital
 * www.cellpointdigital.com
 *
 */
package addons.core

import org.gradle.api.Plugin
import org.gradle.api.Project

abstract class AddonsPlugin implements Plugin<Project> {

    @Override
    final void apply(Project project) {
        AddonsPath.configure(project)
        applyTo(project)
    }

    protected abstract void applyTo(Project project)
}
