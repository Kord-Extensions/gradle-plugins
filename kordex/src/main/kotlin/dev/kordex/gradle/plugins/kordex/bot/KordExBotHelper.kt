/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.kordex.gradle.plugins.kordex.bot

import dev.kordex.gradle.plugins.kordex.VersionContainer
import dev.kordex.gradle.plugins.kordex.base.KordExExtension
import dev.kordex.gradle.plugins.kordex.helpers.ApplicationPluginHelper
import org.gradle.api.Project
import org.gradle.api.tasks.SourceSetContainer
import java.util.*

object KordExBotHelper {
	fun process(target: Project, extension: KordExExtension, versions: VersionContainer) {
		ApplicationPluginHelper.apply(target, extension)

		addGeneratedFiles(target, extension, versions)
	}

	private fun addGeneratedFiles(
		target: Project,
		extension: KordExExtension,
		versions: VersionContainer
	) {
		val outputDir = target.layout.buildDirectory.dir("generated")
		val outputFile = target.layout.buildDirectory.file("generated/kordex.properties")

		val task = target.tasks.create("generateMetadata") {
			group = "generation"
			description = "Generate KordEx metadata."

			inputs.property("settings.dataCollection", extension.bot.dataCollection)
			inputs.property("modules", extension.modules)
			inputs.property("versions.kordEx", versions.kordEx.version)
			inputs.property("versions.kord", versions.kord?.version)

			outputs.file(outputFile)

			doLast {
				val properties = Properties()

				properties.setProperty(
					"settings.dataCollection",
					extension.bot.dataCollection.orNull?.readable.toString()
				)

				properties.setProperty("modules", extension.modules.get().joinToString())
				properties.setProperty("versions.kordEx", versions.kordEx.version)
				properties.setProperty("versions.kord", versions.kord?.version)

				properties.store(outputFile.get().asFile.writer(), null)
			}
		}

		target.tasks
			.getByName("build")
			.dependsOn(task)

		val sourceSet = target
			.extensions
			.getByType(SourceSetContainer::class.java)
			.first { it.name == "main" }

		sourceSet.output.dir(
			mapOf("builtBy" to task),

			outputDir,
		)
	}
}
