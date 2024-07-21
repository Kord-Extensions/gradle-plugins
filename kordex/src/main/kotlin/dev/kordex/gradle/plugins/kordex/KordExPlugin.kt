/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.kordex.gradle.plugins.kordex

import com.jcabi.manifests.Manifests
import dev.kordex.gradle.plugins.kordex.extensions.KordExExtension
import dev.kordex.gradle.plugins.kordex.resolvers.GradleMetadataResolver
import dev.kordex.gradle.plugins.kordex.resolvers.MavenMetadataResolver
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.kotlin.dsl.*
import org.slf4j.LoggerFactory
import java.util.*

class KordExPlugin : Plugin<Project> {
	private val logger = LoggerFactory.getLogger("KordExPlugin")
	private val kotlinJarRegex = "kotlin-compiler-[a-z]+-(.+)\\.jar".toRegex()

	private val gradleResolver = GradleMetadataResolver()
	private val mavenResolver = MavenMetadataResolver()

	override fun apply(target: Project) {
		val extension = target.extensions.create<KordExExtension>("kordEx")

		checkKotlinVersion(target, extension)

		addRepos(target, extension)
		addDependencies(target, extension)
	}

	private fun addRepos(target: Project, extension: KordExExtension) {
		if (!extension.addRepositories) {
			return
		}

		target.repositories {
			mavenCentral()

			maven(S01_BASE)
			maven(OSS_BASE)
		}
	}

	private fun addDependencies(target: Project, extension: KordExExtension) {
		val kordReleases = mavenResolver.getKordReleases()
		val kordSnapshots = mavenResolver.getKordSnapshots()
		val kordExReleases = mavenResolver.getKordExReleases()
		val kordExSnapshots = mavenResolver.getKordExSnapshots()

		val latestKordVersion = maxOf(kordReleases, kordSnapshots) { left, right ->
			left.versioning.lastUpdated.toLong().compareTo(right.versioning.lastUpdated.toLong())
		}

		val latestKordExVersion = maxOf(kordExReleases, kordExSnapshots) { left, right ->
			left.versioning.lastUpdated.toLong().compareTo(right.versioning.lastUpdated.toLong())
		}

		val kordExVersion = if (extension.kordExVersion == null || extension.kordExVersion == "latest") {
			latestKordExVersion.versioning.latest
				?: latestKordVersion.versioning.version
				?: latestKordVersion.version
		} else {
			extension.kordExVersion
		}!!

		val kordVersion = when (extension.kordVersion) {
			null -> {
				val kordExGradle = gradleResolver.kordEx(kordExVersion)

				kordExGradle.variants
					.first { it.name == "runtimeElements" }
					.dependencies
					.first { it.module == "kord-core-voice" }
					.version["requires"]
			}

			"latest" -> latestKordVersion.versioning.latest
				?: latestKordVersion.versioning.version
				?: latestKordVersion.version

			else -> extension.kordVersion
		}

		target.afterEvaluate {
			dependencies {
				add(
					"implementation",
					"com.kotlindiscord.kord.extensions:kord-extensions:$kordExVersion"
				) {
					exclude("dev.kord", "kord-core-voice")
				}

				if (kordVersion != null) {
					if (extension.voice) {
						add(
							"implementation",
							"dev.kord:kord-core-voice:$kordVersion"
						)
					} else {
						add(
							"implementation",
							"dev.kord:kord-core:$kordVersion"
						)
					}
				}

				extension.modules.forEach { module ->
					add(
						"implementation",
						"com.kotlindiscord.kord.extensions:$module:$kordExVersion"
					) {
						exclude("com.kotlindiscord.kord.extensions", "kord-extensions")
					}

					when (module) {
						"adapter-mongodb" -> {
							val mongoLatest = mavenResolver.getMetadata(
								"$CENTRAL_BASE/org/mongodb/mongodb-driver-kotlin-coroutine/maven-metadata.xml"
							).versioning.latest!!

							add(
								"implementation",
								"org.mongodb:mongodb-driver-kotlin-coroutine:$mongoLatest"
							)

							add(
								"implementation",
								"org.mongodb:bson-kotlinx:$mongoLatest"
							)
						}

						"extra-mappings" -> {
							target.repositories {
								maven("https://maven.fabricmc.net`")
								maven("https://maven.quiltmc.org/repository/release")
								maven("https://maven.quiltmc.org/repository/snapshot")
								maven("https://maven.shedaniel.me")
								maven("https://jitpack.io")
							}
						}
					}
				}
			}
		}

		addGeneratedFiles(target, extension, kordVersion, kordExVersion)
	}

	private fun checkKotlinVersion(target: Project, extension: KordExExtension) {
		val checkTask = target.tasks.register("checkKotlinVersion") {
			group = "verification"
			description = "Check whether the correct Kotlin plugin version is in use."

			doLast {
				val wantedVersion = Manifests.read("Kotlin-Version")
				val kotlinPlugin = target.pluginManager.findPlugin("org.jetbrains.kotlin.jvm")

				if (kotlinPlugin == null) {
					logger.warn("WARNING | Unable to find the Kotlin JVM plugin. Is it applied?")
					return@doLast
				}

				val kotlinJarName = target.buildscript.configurations
					.getByName("classpath")
					.first {
						kotlinJarRegex.matches(it.name)
					}.name

				val version = kotlinJarRegex.matchEntire(kotlinJarName)!!.groupValues[1]

				if (!version.equals(wantedVersion, true)) {
					if (extension.ignoreIncompatibleKotlinVersion) {
						logger.warn(
							"Incompatible Kotlin plugin version $version found - expected $wantedVersion"
						)
					} else {
						throw GradleException(
							"Incompatible Kotlin plugin version $version found - expected $wantedVersion"
						)
					}
				}
			}
		}

		target.tasks
			.getByName("check")
			.finalizedBy(checkTask)
	}

	private fun addGeneratedFiles(
		target: Project,
		extension: KordExExtension,
		kordVersion: String?,
		kordExVersion: String
	) {
		val outputDir = target.layout.buildDirectory.file("/generated")
		val outputFile = target.layout.buildDirectory.file("/generated/kordex.properties")

		val task = target.tasks.create("generateMetadata") {
			group = "generation"
			description = "Generate KordEx metadata."

			outputs.file(outputFile)

			doLast {
				val properties = Properties()

				properties.setProperty("settings.dataCollection", extension.dataCollection.readable)
				properties.setProperty("modules", extension.modules.joinToString())
				properties.setProperty("versions.kordEx", kordExVersion)
				properties.setProperty("versions.kord", kordVersion)

				properties.store(outputFile.get().asFile.writer(), null)
			}
		}

		val sourceSet = target
			.extensions
			.getByType(SourceSetContainer::class.java)
			.first { it.name == "main" }

		sourceSet.output.dir(
			mapOf("builtBy" to task),
			outputDir
		)
	}
}
