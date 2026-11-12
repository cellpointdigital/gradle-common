/**
 * Copyright (c) 2023. Cellpoint Digital
 * www.cellpointdigital.com
 *
 */
package addons.javafeatures

import addons.core.AddonsPlugin
import org.gradle.api.Project

class JavaFeaturesPlugin extends AddonsPlugin {

    @Override
    protected void applyTo(Project project) {
        project.extensions.create('useJavaFeatures', UseJavaFeaturesExtension)
    }

}
