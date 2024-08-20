/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.kordex.gradle.plugins.kordex.functions

import dev.kordex.gradle.plugins.kordex.VersionContainer
import dev.kordex.gradle.plugins.kordex.base.KordExExtension
import org.gradle.api.Project
import org.gradle.api.problems.ProblemReporter
import org.gradle.api.problems.Severity
import org.gradle.api.provider.Provider

// kotlin-gradle-plugin-2.0.20-Beta1-gradle85.jar
private val kotlinJarRegex = "kotlin-gradle-plugin-(.+)-gradle\\d+\\.jar".toRegex()

@Suppress("UnstableApiUsage", "StringLiteralDuplication")
fun Project.checkTask(
	extension: KordExExtension,
	versionsProvider: Provider<VersionContainer>,
	problemReporter: ProblemReporter
) {
	val checkTask = tasks.register("checkKotlinVersion") {
		group = "verification"
		description = "Check whether the correct Kotlin plugin version is in use."

		doLast {
			val kordExGradle = versionsProvider.get().kordExGradle

			val runtimeElements = kordExGradle
				.variants
				.first { it.name == "runtimeElements" }

			val wantedVersion = runtimeElements
				.dependencies
				.first { it.group == "org.jetbrains.kotlin" && it.module.contains("-stdlib-", true) }
				.version["requires"]
				?: runtimeElements
					.dependencyConstraints
					.first { it.group == "org.jetbrains.kotlin" && it.module.contains("-stdlib-", true) }
					.version["requires"]

			if (wantedVersion == null) {
				error("Unable to figure out which Kotlin version is required. Please report this!")
			}

			val kotlinPlugin = pluginManager.findPlugin("org.jetbrains.kotlin.jvm")

			if (kotlinPlugin == null) {
				logger.warn("WARNING | Unable to find the Kotlin JVM plugin. Is it applied?")
				return@doLast
			}

			val classpathJars = plugins.toList()
				.map { it::class.java.protectionDomain.codeSource.location }
				.map { it.path.split("/").last() }

			val kotlinJarName = classpathJars
				.firstOrNull {
					kotlinJarRegex.matches(it)
				}

			if (kotlinJarName == null) {
				logger.warn(
					"WARNING | Kotlin JVM plugin applied, but the JAR couldn't be found. " +
						"Found ${classpathJars.size} JARs:"
				)

				classpathJars.forEach {
					logger.warn("-> $it")
				}

				return@doLast
			}

			val version = kotlinJarRegex.matchEntire(kotlinJarName)!!.groupValues[1]

			if (!version.equals(wantedVersion, true)) {
				if (extension.ignoreIncompatibleKotlinVersion.get()) {
					logger.warn(
						"Incompatible Kotlin plugin found - Kord Extensions version " +
							"${kordExGradle.component.version} expects Kotlin plugin version $wantedVersion"
					)

					problemReporter.reporting {
						id("kotlin-version", "Incompatible Kotlin plugin found")

						details(
							"Incompatible Kotlin plugin found - Kord Extensions version " +
								"${kordExGradle.component.version} expects Kotlin plugin version $wantedVersion"
						)

						solution("Switch Kotlin plugin version to $wantedVersion")
						severity(Severity.WARNING)
					}
				} else {
					problemReporter.throwing {
						withException(
							RuntimeException(
								"Incompatible Kotlin plugin version found - Kord Extensions version " +
									"${kordExGradle.component.version} expects Kotlin plugin version $wantedVersion"
							)
						)

						id("kotlin-version", "Incompatible Kotlin plugin version found")

						details(
							"Incompatible Kotlin plugin found - Kord Extensions version " +
								"${kordExGradle.component.version} expects Kotlin plugin version $wantedVersion"
						)

						solution(
							"Switch Kotlin plugin version to $wantedVersion, or set " +
								"'ignoreIncompatibleKotlinVersion' to 'true' if you know what you're doing"
						)

						severity(Severity.ERROR)
					}
				}
			}
		}
	}

	tasks
		.getByName("check")
		.finalizedBy(checkTask)
}
