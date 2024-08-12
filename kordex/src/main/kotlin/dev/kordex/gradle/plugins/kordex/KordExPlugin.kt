/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

@file:Suppress("StringLiteralDuplication")

package dev.kordex.gradle.plugins.kordex

import dev.kordex.gradle.plugins.kordex.base.*
import dev.kordex.gradle.plugins.kordex.bot.KordExBotHelper
import dev.kordex.gradle.plugins.kordex.plugins.KordExPluginHelper
import dev.kordex.gradle.plugins.kordex.resolvers.GradleMetadataResolver
import dev.kordex.gradle.plugins.kordex.resolvers.gradle.GradleMetadata
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonPrimitive
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaApplication
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.problems.Problems
import org.gradle.api.problems.Severity
import org.gradle.api.tasks.bundling.Jar
import org.gradle.kotlin.dsl.*
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import javax.inject.Inject

@Suppress("UnstableApiUsage")
class KordExPlugin @Inject constructor(problems: Problems) : Plugin<Project> {
	private val kotlinJarRegex = "kotlin-compiler-[a-z]+-(.+)\\.jar".toRegex()

	val problemReporter = problems.forNamespace("dev.kordex")

	override fun apply(target: Project) {
		val extension = target.extensions.create<KordExExtension>("kordEx").apply {
			setup()
		}

		target.afterEvaluate {
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

			val versions = calculateVersions(extension)

			checkKotlinVersion(target, extension, versions.kordExGradle)

			if (extension.hasBot) {
				KordExBotHelper.process(target, extension, versions)
			}

			if (extension.hasPlugin) {
				KordExPluginHelper.process(target, extension)
			}

			configureCompilerPlugins(target, extension, versions.kordExGradle)

			addRepos(target, extension)

			if (extension.addDependencies.orNull == true) {
				addDependencies(target, extension, versions.kordEx, versions.kord)
			} else {
				logger.info("Not configuring dependencies, as `addDependencies` is set to `false` or is missing.")
			}
		}
	}

	private fun calculateVersions(extension: KordExExtension): VersionContainer {
		val kordExVersion = if (!extension.kordExVersion.isPresent || extension.kordExVersion.orNull == "latest") {
			latestKordExMetadata.getCurrentVersion()
		} else {
			extension.kordExVersion.map(::Version).orNull
		}!!

		val kordExGradle = GradleMetadataResolver.kordEx(kordExVersion)

		val kordVersion = when (extension.kordVersion.orNull) {
			null ->
				kordExGradle.variants
					.first { it.name == "runtimeElements" }
					.dependencies
					.first { it.module == "kord-core-voice" }
					.version["requires"]
					?.let { Version(it) }

			"latest" -> latestKordMetadata.getCurrentVersion()

			else -> extension.kordVersion.map(::Version).orNull
		}

		return VersionContainer(kordExVersion, kordVersion, kordExGradle)
	}

	private fun addRepos(target: Project, extension: KordExExtension) {
		if (!extension.addRepositories.get()) {
			return
		}

		target.repositories.google()
		target.repositories.mavenCentral()

		target.repo(KORDEX_RELEASES)
		target.repo(KORDEX_SNAPSHOTS)
		target.repo(S01_BASE)
		target.repo(OSS_BASE)

		val modules = extension.modules.get().normalizeModules()

		if ("extra-mappings" in modules) {
			target.repo("https://maven.fabricmc.net")
			target.repo("https://maven.quiltmc.org/repository/release")
			target.repo("https://maven.quiltmc.org/repository/snapshot")
			target.repo("https://maven.shedaniel.me")
			target.repo("https://jitpack.io")
		}
	}

	private fun addDependencies(
		target: Project,
		extension: KordExExtension,
		kordExVersion: Version,
		kordVersion: Version?
	) {
		val configurations = if (extension.configurations.isPresent && extension.configurations.get().isNotEmpty()) {
			extension.configurations.get().toTypedArray()
		} else if (extension.hasPlugin) {
			arrayOf("compileOnly")
		} else {
			arrayOf("implementation")
		}

		target.afterEvaluate {
			target.pluginManager.withPlugin("com.google.devtools.ksp") {
				logger.info("KSP plugin detected, adding Kord Extensions annotation processor")

				target.addDependency(
					arrayOf("ksp"),
					"com.kotlindiscord.kord.extensions:annotation-processor:$kordExVersion"
				)
			}

			target.addDependency(
				configurations,
				"com.kotlindiscord.kord.extensions:kord-extensions:$kordExVersion"
			) { exclude("dev.kord", "kord-core-voice") }

			if (kordVersion != null) {
				@Suppress("UnnecessaryParentheses")  // Reads better
				if (extension.hasPlugin || (extension.hasBot && extension.bot.voice.get())) {
					target.addDependency(
						configurations,
						"dev.kord:kord-core-voice:$kordVersion"
					)
				} else {
					target.addDependency(
						configurations,
						"dev.kord:kord-core:$kordVersion"
					)
				}
			}

			extension.modules.get().normalizeModules().forEach { module ->
				target.addDependency(
					configurations,
					"com.kotlindiscord.kord.extensions:$module:$kordExVersion"
				) {
					exclude("com.kotlindiscord.kord.extensions", "kord-extensions")
				}

				if (module == "adapter-mongodb") {
					val mongoLatest = latestMongoDBMetadata.versioning.latest!!

					target.addDependency(
						configurations,
						"org.mongodb:mongodb-driver-kotlin-coroutine:$mongoLatest"
					)

					target.addDependency(
						configurations,
						"org.mongodb:bson-kotlinx:$mongoLatest"
					)
				}
			}
		}
	}

	private fun checkKotlinVersion(target: Project, extension: KordExExtension, kordExGradle: GradleMetadata) {
		val wantedVersion = kordExGradle
			.variants
			.first { it.name == "runtimeElements" }
			.dependencies
			.first { it.group == "org.jetbrains.kotlin" && it.module.contains("-stdlib-", true) }
			.version["requires"]!!

		val checkTask = target.tasks.register("checkKotlinVersion") {
			group = "verification"
			description = "Check whether the correct Kotlin plugin version is in use."

			doLast {
				val kotlinPlugin = target.pluginManager.findPlugin("org.jetbrains.kotlin.jvm")

				if (kotlinPlugin == null) {
					logger.warn("WARNING | Unable to find the Kotlin JVM plugin. Is it applied?")
					return@doLast
				}

				val kotlinJarName = target.buildscript.configurations
					.getByName("classpath")
					.firstOrNull {
						kotlinJarRegex.matches(it.name)
					}?.name

				if (kotlinJarName == null) {
					logger.warn("WARNING | Kotlin JVM plugin applied, but the JAR couldn't be found. Found JARs:")

					target.buildscript.configurations.getByName("classpath").forEach {
						logger.warn("-> ${it.name}")
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

		target.tasks
			.getByName("check")
			.finalizedBy(checkTask)
	}

	private fun configureCompilerPlugins(target: Project, extension: KordExExtension, kordExGradle: GradleMetadata) {
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

		if (extension.hasBot && extension.bot.mainClass.isPresent) {
			target.tasks.withType<Jar> {
				manifest {
					attributes(
						"Main-Class" to extension.bot.mainClass.get()
					)
				}
			}

			target.extensions.configure<JavaApplication> {
				mainClass = extension.bot.mainClass
			}
		}
	}
}
