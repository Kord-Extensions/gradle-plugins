/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.kordex.gradle.plugins.kordex.plugins

import com.github.zafarkhaja.semver.ParseException
import com.github.zafarkhaja.semver.expr.ExpressionParser
import dev.kordex.gradle.plugins.kordex.base.KordExExtension
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.distribution.DistributionContainer
import org.gradle.api.distribution.plugins.DistributionPlugin
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.api.plugins.UnknownPluginException
import org.gradle.kotlin.dsl.getByType
import java.util.*

object KordExPluginHelper {
	fun process(target: Project, extension: KordExExtension) {
		validate(target, extension)

		target.pluginManager.apply(DistributionPlugin::class.java)

		target.afterEvaluate {
			val distributionTarTask = tasks.getByName("distTar")
			val distributionZipTask = tasks.getByName("distZip")

			val jarTask = target.tasks.getByName("jar")
			val resourcesTask = target.tasks.getByName("processResources")

			val metadataTask = metadataTask(target, extension)

			distributionTarTask.dependsOn(jarTask, resourcesTask, metadataTask)
			distributionZipTask.dependsOn(jarTask, resourcesTask, metadataTask)

			val configuration = target.configurations.getByName("runtimeClasspath")

			val distributions = target.extensions.getByType<DistributionContainer>()
			val distribution = distributions.getByName("main")

			distribution.contents {
				into("/classes") {
					from(resourcesTask)

					jarTask.outputs.files.forEach {
						from(zipTree(it))

						duplicatesStrategy = DuplicatesStrategy.WARN
					}
				}

				into("/lib") {
					configuration.files.forEach {
						from(it) {
							duplicatesStrategy = DuplicatesStrategy.WARN
						}
					}
				}

				into("/") {
					from(metadataTask) {
						duplicatesStrategy = DuplicatesStrategy.INCLUDE
					}
				}
			}
		}
	}

	fun validate(target: Project, extension: KordExExtension) {
		try {
			target.plugins.getPlugin("application")

			error(
				"Project ${target.name} has the `application` plugin applied, which will interfere with your " +
					"Kord Extensions plugin."
			)
		} catch (e: UnknownPluginException) {
			// Nothing, this is what we want.
		}

		val requiredProperties = mapOf(
			"plugin -> pluginClass" to extension.plugin.pluginClass,
			"plugin -> id" to extension.plugin.id,
			"plugin -> version" to extension.plugin.version,
		)

		requiredProperties.forEach { (key, value) ->
			if (!value.isPresent) {
				error("Required property $key has not been set.")
			}
		}

		try {
			ExpressionParser.newInstance().parse(extension.plugin.version.get())
		} catch (e: ParseException) {
			throw IllegalStateException("Unable to parse plugin version ${extension.plugin.version.get()}", e)
		}
	}

	fun metadataTask(target: Project, extension: KordExExtension): Task {
		val outputFile = target.layout.buildDirectory.file("generated/plugin.properties")

		return target.tasks.create("generatePluginMetadata") {
			group = "generation"
			description = "Generate plugin metadata."

			outputs.file(outputFile)

			doLast {
				val properties = Properties()

				properties.setProperty("plugin.class", extension.plugin.pluginClass.get())
				properties.setProperty("plugin.id", extension.plugin.id.get())
				properties.setProperty("plugin.version", extension.plugin.version.get())

				if (extension.plugin.author.isPresent) {
					properties.setProperty("plugin.provider", extension.plugin.author.get())
				}

				if (!extension.plugin.dependencies.orNull.isNullOrEmpty()) {
					properties.setProperty("plugin.dependencies", extension.plugin.dependencies.get().joinToString())
				}

				if (extension.plugin.description.isPresent) {
					properties.setProperty("plugin.description", extension.plugin.description.get())
				}

				if (extension.plugin.license.isPresent) {
					properties.setProperty("plugin.license", extension.plugin.license.get())
				}

				if (extension.plugin.kordExVersionSpecifier.isPresent) {
					properties.setProperty("plugin.requires", extension.plugin.kordExVersionSpecifier.get())
				}

				properties.store(outputFile.get().asFile.writer(), null)
			}
		}
	}
}
