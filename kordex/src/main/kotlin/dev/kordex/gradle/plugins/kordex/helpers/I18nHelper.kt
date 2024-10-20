/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.kordex.gradle.plugins.kordex.helpers

import dev.kordex.gradle.plugins.kordex.base.KordExExtension
import dev.kordex.i18n.generator.TranslationsClass
import org.gradle.api.Project
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.kotlin.dsl.*
import java.nio.file.Files
import java.util.Properties

object I18nHelper {
	fun apply(target: Project, extension: KordExExtension) {
		validate(extension)

		var bundle = extension.i18n.translationBundle.get().split(".")

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

		val outputDirectory = extension.i18n.outputDirectory.orNull
			?: target.layout.buildDirectory.file("generated/kordex/main/kotlin/").get().asFile

		val generateTask = target.tasks.create("generateTranslationsClass") {
			group = "generation"
			description = "Generate classes containing translation keys."

			inputs.file(inputFile)

			doLast {
				val props = Properties()

				props.load(
					Files.newBufferedReader(
						inputFile.toPath(),
						Charsets.UTF_8
					)
				)

				val translationsClass = TranslationsClass(
					bundle = bundle.joinToString("."),
					allProps = props,
					className = extension.i18n.className.get(),
					classPackage = extension.i18n.classPackage.get()
				)

				translationsClass.writeTo(outputDirectory)
			}
		}

		target.tasks.getByName("build") {
			dependsOn(generateTask)
		}

		if (extension.i18n.configureSourceSet.get()) {
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

	fun validate(extension: KordExExtension) {
		val requiredProperties = mapOf(
			"i18n -> classPackage" to extension.i18n.classPackage,
			"i18n -> translationBundle" to extension.i18n.translationBundle,
		)

		requiredProperties.forEach { (key, value) ->
			if (!value.isPresent) {
				error("Required property $key has not been set.")
			}
		}
	}
}
