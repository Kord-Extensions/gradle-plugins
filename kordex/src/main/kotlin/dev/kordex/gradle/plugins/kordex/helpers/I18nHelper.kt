/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.kordex.gradle.plugins.kordex.helpers

import dev.kordex.gradle.plugins.kordex.InternalAPI
import dev.kordex.gradle.plugins.kordex.i18n.KordExI18nSettings
import dev.kordex.i18n.generator.MESSAGE_FORMAT_VERSIONS
import dev.kordex.i18n.generator.TranslationsClass
import org.gradle.api.Project
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.kotlin.dsl.*
import java.nio.charset.MalformedInputException
import java.nio.file.Files
import java.util.Properties

@InternalAPI
object I18nHelper {
	fun apply(target: Project, extension: KordExI18nSettings) {
		validate(extension)

		var bundle = extension.translationBundle.get().split(".")

		if (bundle.size == 1) {
			bundle = bundle + "strings"
		}

		val sourceSet = target.extensions.getByType<SourceSetContainer>().named("main")

		val inputFile = target.layout.projectDirectory
			.dir("src/main/resources/translations/${bundle.first()}")
			.file("${bundle[1]}.properties")
			.asFile

		if (!inputFile.exists()) {
			error("Cannot find translation bundle file: ${inputFile.absolutePath}")
		}

		val outputDirectory = extension.outputDirectory.orNull
			?: target.layout.buildDirectory.file("generated/kordex/main/kotlin/").get().asFile

		val generateTask = target.tasks.register("generateTranslationsClass") {
			group = "generation"
			description = "Generate classes containing translation keys."

			inputs.file(inputFile)

			doLast {
				try {
					val props = Properties()

					props.load(
						Files.newBufferedReader(
							inputFile.toPath(),
							Charsets.UTF_8
						)
					)

					@Suppress("DEPRECATION")
					val translationsClass = TranslationsClass(
						bundle = bundle.joinToString("."),
						allProps = props,
						className = extension.className.get(),
						classPackage = extension.classPackage.get(),
						publicVisibility = extension.publicVisibility.get(),
						messageFormatVersion = extension.messageFormatVersion.get(),
					)

					translationsClass.writeTo(outputDirectory)
				} catch (e: MalformedInputException) {
					error(
						"Malformed input exception: ${e.message} - " +
							"Check that your translation bundle is UTF-8 encoded!",
					)
				}
			}
		}

		target.tasks.getByName("compileKotlin") {
			dependsOn(generateTask)
		}

		if (extension.configureSourceSet.get()) {
			sourceSet {
				java {
					srcDir(outputDirectory)
				}

				output.dir(
					mapOf("builtBy" to generateTask),
					inputFile
				)
			}
		}
	}

	fun validate(extension: KordExI18nSettings) {
		val requiredProperties = mapOf(
			"i18n -> classPackage" to extension.classPackage,
			"i18n -> translationBundle" to extension.translationBundle,
		)

		requiredProperties.forEach { (key, value) ->
			if (!value.isPresent) {
				error("Required property $key has not been set.")
			}
		}

		if (extension.messageFormatVersion.get() !in MESSAGE_FORMAT_VERSIONS) {
			error(
				"Invalid message format version ${extension.messageFormatVersion} - " +
					"must be one of ${MESSAGE_FORMAT_VERSIONS.joinToString()}"
			)
		}
	}
}
