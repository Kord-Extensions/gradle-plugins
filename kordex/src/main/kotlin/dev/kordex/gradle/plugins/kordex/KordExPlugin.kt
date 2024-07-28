/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

@file:Suppress("StringLiteralDuplication")

package dev.kordex.gradle.plugins.kordex

import dev.kordex.gradle.plugins.kordex.extensions.KordExExtension
import dev.kordex.gradle.plugins.kordex.resolvers.GradleMetadataResolver
import dev.kordex.gradle.plugins.kordex.resolvers.MavenMetadataResolver
import dev.kordex.gradle.plugins.kordex.resolvers.gradle.GradleMetadata
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonPrimitive
import org.gradle.api.GradleException
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ApplicationPlugin
import org.gradle.api.plugins.JavaApplication
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.bundling.Jar
import org.gradle.kotlin.dsl.*
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.slf4j.LoggerFactory
import java.util.*

class KordExPlugin : Plugin<Project> {
	@Suppress("UnusedPrivateProperty")  // For now...
	private val logger = LoggerFactory.getLogger("KordExPlugin")

	private val kotlinJarRegex = "kotlin-compiler-[a-z]+-(.+)\\.jar".toRegex()

	private val gradleResolver = GradleMetadataResolver()
	private val mavenResolver = MavenMetadataResolver()

	private val kordReleases = mavenResolver.getKordReleases()
	private val kordSnapshots = mavenResolver.getKordSnapshots()
	private val kordExReleases = mavenResolver.getKordExReleases()
	private val kordExSnapshots = mavenResolver.getKordExSnapshots()

	private val latestKordMetadata = maxOf(kordReleases, kordSnapshots) { left, right ->
		left.getCurrentVersion().compareTo(right.getCurrentVersion())
	}

	private val latestKordExMetadata = maxOf(kordExReleases, kordExSnapshots) { left, right ->
		left.getCurrentVersion().compareTo(right.getCurrentVersion())
	}

	override fun apply(target: Project) {
		val extension = target.extensions.create<KordExExtension>("kordEx").apply {
			addRepositories.convention(true)
			ignoreIncompatibleKotlinVersion.convention(false)
			voice.convention(true)
		}
		val (kordExVersion, kordVersion, kordExGradle) = calculateVersions(extension)

		target.afterEvaluate {
			if (extension.mainClass.isPresent) {
				target.pluginManager.apply(ApplicationPlugin::class.java)
			}

			configureCompilerPlugins(target, extension, kordExGradle)
		}

		checkKotlinVersion(target, extension, kordExGradle)

		addRepos(target, extension)
		addDependencies(target, extension, kordExVersion, kordVersion)
	}

	private fun calculateVersions(extension: KordExExtension): Triple<Version, Version?, GradleMetadata> {
		val kordExVersion = if (!extension.kordExVersion.isPresent || extension.kordExVersion.orNull == "latest") {
			latestKordExMetadata.getCurrentVersion()
		} else {
			extension.kordExVersion.map(::Version).orNull
		}!!

		val kordExGradle = gradleResolver.kordEx(kordExVersion)

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

		return Triple(kordExVersion, kordVersion, kordExGradle)
	}

	private fun addRepos(target: Project, extension: KordExExtension) {
		if (!extension.addRepositories.get()) {
			return
		}

		target.repositories {
			google()
			mavenCentral()

			maven(KORDEX_RELEASES)
			maven(KORDEX_SNAPSHOTS)

			maven(S01_BASE)
			maven(OSS_BASE)
		}
	}

	private fun addDependencies(
		target: Project,
		extension: KordExExtension,
		kordExVersion: Version,
		kordVersion: Version?
	) {
		target.afterEvaluate {
			dependencies {
				add(
					"implementation",
					"com.kotlindiscord.kord.extensions:kord-extensions:$kordExVersion"
				) {
					exclude("dev.kord", "kord-core-voice")
				}

				if (kordVersion != null) {
					if (extension.voice.get()) {
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

				extension.modules.get().forEach { module ->
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
					.first {
						kotlinJarRegex.matches(it.name)
					}.name

				val version = kotlinJarRegex.matchEntire(kotlinJarName)!!.groupValues[1]

				if (!version.equals(wantedVersion, true)) {
					if (extension.ignoreIncompatibleKotlinVersion.get()) {
						logger.warn(
							"WARNING | Incompatible Kotlin plugin version $version found - Kord Extensions " +
								"version ${kordExGradle.component.version} expects Kotlin version $wantedVersion"
						)
					} else {
						throw GradleException(
							"Incompatible Kotlin plugin version $version found - Kord Extensions version " +
								"${kordExGradle.component.version} expects Kotlin version $wantedVersion"
						)
					}
				}
			}
		}

		target.tasks
			.getByName("check")
			.finalizedBy(checkTask)
	}

	private fun configureCompilerPlugins(target: Project, extension: KordExExtension, kordExGradle: GradleMetadata) {
		val versionElement = kordExGradle
			.variants
			.first { it.name == "apiElements" }
			.attributes?.get("org.gradle.jvm.version")
			?: kordExGradle
				.variants
				.first { it.name == "runtimeElements" }
				.attributes?.get("org.gradle.jvm.version")

		val javaVersion = versionElement?.jsonPrimitive?.int

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

		if (extension.mainClass.isPresent) {
			target.tasks.withType<Jar> {
				manifest {
					attributes(
						"Main-Class" to extension.mainClass
					)
				}
			}

			target.extensions.configure<JavaApplication> {
				mainClass = extension.mainClass
			}
		}
	}

	private fun addGeneratedFiles(
		target: Project,
		extension: KordExExtension,
		kordVersion: Version?,
		kordExVersion: Version,
	) {
		val outputDir = target.layout.buildDirectory.dir("generated")
		val outputFile = target.layout.buildDirectory.file("generated/kordex.properties")

		val task = target.tasks.create("generateMetadata") {
			group = "generation"
			description = "Generate KordEx metadata."

			outputs.file(outputFile)

			doLast {
				val properties = Properties()

				properties.setProperty(
					"settings.dataCollection",
					extension.dataCollection.orNull?.readable.toString()
				)

				properties.setProperty("modules", extension.modules.get().joinToString())
				properties.setProperty("versions.kordEx", kordExVersion.version)
				properties.setProperty("versions.kord", kordVersion?.version)

				properties.store(outputFile.get().asFile.writer(), null)
			}
		}

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
