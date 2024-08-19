/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.kordex.gradle.plugins.docker

import dev.kordex.gradle.plugins.docker.extensions.DockerExtension
import dev.kordex.gradle.plugins.docker.tasks.CreateDockerfileTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.register

class DockerPlugin : Plugin<Project> {
	override fun apply(target: Project) {
		val extension = target.extensions.create<DockerExtension>("docker")

		val task = target.tasks.register<CreateDockerfileTask>("createDockerfile") {
			group = "generation"
			description = "Generate a Dockerfile, as configured."

			// Should fix skipping generation, but probably better solved by checking inputs
			// and moving to properties in the extension.
			outputs.upToDateWhen { false }

			file = extension.target

			doFirst {
				extension.commandsBuilder(dockerFile.commands)
				extension.directives.forEach(dockerFile.directives::set)
			}
		}

		if (extension.generateOnBuild) {
			target.tasks.getByName("build") {
				finalizedBy(task)
			}
		}
	}
}
