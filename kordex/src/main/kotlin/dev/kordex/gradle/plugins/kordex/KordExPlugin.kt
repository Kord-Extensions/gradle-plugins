/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

@file:Suppress("StringLiteralDuplication")

package dev.kordex.gradle.plugins.kordex

import dev.kordex.gradle.plugins.kordex.base.*
import dev.kordex.gradle.plugins.kordex.bot.KordExBotHelper
import dev.kordex.gradle.plugins.kordex.functions.checkTask
import dev.kordex.gradle.plugins.kordex.functions.configurationsProvider
import dev.kordex.gradle.plugins.kordex.functions.packageProvider
import dev.kordex.gradle.plugins.kordex.functions.versionsProvider
import dev.kordex.gradle.plugins.kordex.plugins.KordExPluginHelper
import dev.kordex.gradle.plugins.kordex.resolvers.gradle.GradleMetadata
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonPrimitive
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.dsl.DependencyFactory
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.problems.Problems
import org.gradle.api.problems.Severity
import org.gradle.api.provider.ProviderFactory
import org.gradle.kotlin.dsl.*
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import javax.inject.Inject

@Suppress("UnstableApiUsage")
class KordExPlugin @Inject constructor(
	problems: Problems,
	val dependencies: DependencyFactory,
	val providers: ProviderFactory,
) : Plugin<Project> {
	val problemReporter = problems.forNamespace("dev.kordex")

	@Suppress("UnnecessaryParentheses")
	override fun apply(target: Project) {
		val extension = target.extensions.create<KordExExtension>("kordEx").apply {
			setup()
		}

		val versionsProvider = providers.versionsProvider(extension)
		val packageProvider = providers.packageProvider(versionsProvider)
		val configurationsProvider = providers.configurationsProvider(extension)

		target.checkTask(extension, versionsProvider, problemReporter)

		target.configurations.all {
			dependencies.addAllLater(
				providers.provider {
					val deps = mutableListOf<Dependency>()

					val configurations = configurationsProvider.get()
					val packages = packageProvider.get()
					val versions = versionsProvider.get()

					if (name in configurations) {
						deps.add(
							dep("${packages.base}:kord-extensions:${versions.kordEx}")
								.exclude("dev.kord", "kord-core-voice")
						)

						if (versions.kord != null) {
							if (extension.hasPlugin || (extension.hasBot && extension.bot.voice.get())) {
								deps.add(
									dep("dev.kord:kord-core-voice:${versions.kord}")
								)
							} else {
								deps.add(
									dep("dev.kord:kord-core:${versions.kord}")
								)
							}
						}

						extension.modules.get().normalizeModules(versions.kordEx).forEach { module ->
							deps.add(
								dep("${packages.module}:$module:${versions.kordEx}")
									.exclude(packages.base, "kord-extensions")
							)

							if (module in MONGODB_MODULES) {
								val mongoLatest = latestMongoDBMetadata?.versioning?.latest
									?: error("Unable to resolve MongoDB release metadata. Please report this!")

								deps.add(
									dep("org.mongodb:mongodb-driver-kotlin-coroutine:$mongoLatest")
								)

								deps.add(
									dep("org.mongodb:bson-kotlinx:$mongoLatest")
								)
							}
						}
					}

					if (name == "ksp") {
						deps.add(
							dep("${packages.base}:annotation-processor:${versions.kordEx}")
						)
					}

// 					println("Configuration: $name (${deps.size} dependencies)")
// 					deps.forEach { println(" -> ${it.group}:${it.name}:${it.version}") }

					deps
				}
			)
		}

		target.afterEvaluate {
			val versions = versionsProvider.get()

			if (extension.hasBot && extension.hasPlugin) {
				problemReporter.throwing {
					withException(
						RuntimeException(
							"Project is both bot and plugin - if you need both in the same project, split them into " +
								"separate Gradle subprojects"
						)
					)

					id("both-bot-and-plugin", "Project is both bot and plugin")
					details("Project ${target.name} cannot be both a bot and a plugin")
					solution("If you need both in the same project, split them into separate Gradle subprojects")
					severity(Severity.ERROR)
				}
			}

			if (extension.hasBot) {
				KordExBotHelper.process(target, extension, versions)
			}

			if (extension.hasPlugin) {
				KordExPluginHelper.process(target, extension)
			}

			configurePlugins(target, extension, versions.kordExGradle)
			addRepos(target, extension, versions.kordEx)
		}
	}

	private fun dep(coordinate: String) =
		dependencies.create(coordinate)

	private fun addRepos(target: Project, extension: KordExExtension, kordExVersion: Version) {
		if (!extension.addRepositories.get()) {
			return
		}

		target.repositories.google()
		target.repositories.mavenCentral()

		target.repo(KORDEX_RELEASES)
		target.repo(KORDEX_SNAPSHOTS)
		target.repo(S01_BASE)
		target.repo(OSS_BASE)

		val modules = extension.modules.get().normalizeModules(kordExVersion, log = false)

		if (MAPPINGS_V1 in modules || MAPPINGS_V2 in modules) {
			target.repo("https://maven.fabricmc.net")
			target.repo("https://maven.quiltmc.org/repository/release")
			target.repo("https://maven.quiltmc.org/repository/snapshot")
			target.repo("https://maven.shedaniel.me")
			target.repo("https://jitpack.io")
		}
	}

	private fun configurePlugins(target: Project, extension: KordExExtension, kordExGradle: GradleMetadata) {
		val javaVersion = if (extension.jvmTarget.isPresent) {
			extension.jvmTarget.get()
		} else {
			val versionElement = kordExGradle
				.variants
				.first { it.name == "apiElements" }
				.attributes?.get("org.gradle.jvm.version")
				?: kordExGradle
					.variants
					.first { it.name == "runtimeElements" }
					.attributes?.get("org.gradle.jvm.version")

			versionElement?.jsonPrimitive?.int
		}

		target.tasks.withType<KotlinCompile> {
			compilerOptions {
				optIn.add("kotlin.RequiresOptIn")

				if (javaVersion != null) {
					jvmTarget.set(JvmTarget.fromTarget(javaVersion.toString()))
				}
			}
		}

		target.extensions.configure<JavaPluginExtension> {
			if (javaVersion != null) {
				sourceCompatibility = JavaVersion.toVersion(javaVersion.toString())
				targetCompatibility = JavaVersion.toVersion(javaVersion.toString())
			}
		}
	}
}
