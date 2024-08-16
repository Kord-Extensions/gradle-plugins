/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.kordex.gradle.plugins.kordex.helpers

import dev.kordex.gradle.plugins.kordex.base.KordExExtension
import org.gradle.api.Project
import org.gradle.api.plugins.ApplicationPlugin
import org.gradle.api.plugins.JavaApplication
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.bundling.Jar
import org.gradle.kotlin.dsl.*
import org.slf4j.LoggerFactory
import java.io.File
import kotlin.jvm.java

object ApplicationPluginHelper {
	private val logger = LoggerFactory.getLogger(ApplicationPluginHelper::class.java)

	fun apply(target: Project, extension: KordExExtension) {
		target.plugins.apply(ApplicationPlugin::class.java)

		target.extensions.configure<JavaApplication> {
			mainClass = extension.bot.mainClass
		}

		target.tasks.withType<Jar> {
			manifest {
				attributes(
					"Main-Class" to extension.bot.mainClass.get()
				)
			}
		}

		val sourceSets = target.extensions.getByType<SourceSetContainer>()

		target.tasks.create<JavaExec>("dev") {
			group = "application"
			description = "Run the configured Kord Extensions bot in development mode"

			environment(processDotEnv(target, extension))

			jvmArguments.add("-DdevMode=true")

			classpath = sourceSets.named("main").get().runtimeClasspath
			mainClass = extension.bot.mainClass
		}
	}

	fun processDotEnv(target: Project, extension: KordExExtension): MutableMap<String, String> {
		val variables = mutableMapOf<String, String>()

		if (!extension.bot.processDotEnv.get()) {
			return variables
		}

		val files = mutableListOf<File>()

		val top = target.rootProject.projectDir.toPath()
		var current = target.projectDir.toPath()

		while (current.startsWith(top)) {
			val file = current.resolve(".env").toFile()

			if (file.isFile) {
				files.add(file)
			}

			current = current.parent
		}

		files.reverse()

		files.forEach { file ->
			logger.info("Loading variables from: $file")

			val lines = file.readLines()

			for (line in lines) {
				var effectiveLine = line.trimStart()

				if (effectiveLine.isBlank() || effectiveLine.startsWith("#")) {
					continue
				}

				if (effectiveLine.contains("#")) {
					effectiveLine = effectiveLine.substring(0, effectiveLine.indexOf("#"))
				}

				if (!effectiveLine.contains('=')) {
					logger.warn(
						"Invalid line in dotenv file: \"=\" not found\n" +
							" -> $file\n" +
							" -> $effectiveLine"
					)

					continue
				}

				val split = effectiveLine
					.split("=", limit = 2)
					.map { it.trim() }

				if (split.size != 2) {
					logger.warn(
						"Invalid line in dotenv file: variables must be of the form \"name=value\"\n" +
							" -> $file\n" +
							" -> $effectiveLine"
					)

					continue
				}

				logger.trace("${split[0]} -> ${split[1]}")

				variables[split[0]] = split[1]
			}
		}

		return variables
	}
}
