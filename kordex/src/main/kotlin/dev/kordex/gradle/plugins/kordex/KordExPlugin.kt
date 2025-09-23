/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

@file:Suppress("StringLiteralDuplication")
@file:OptIn(InternalAPI::class)

package dev.kordex.gradle.plugins.kordex

import dev.kordex.gradle.plugins.kordex.base.KordExExtension
import dev.kordex.gradle.plugins.kordex.base.latestMongoDBMetadata
import dev.kordex.gradle.plugins.kordex.base.normalizeModules
import dev.kordex.gradle.plugins.kordex.base.repo
import dev.kordex.gradle.plugins.kordex.bot.KordExBotHelper
import dev.kordex.gradle.plugins.kordex.functions.checkTask
import dev.kordex.gradle.plugins.kordex.functions.configurationsProvider
import dev.kordex.gradle.plugins.kordex.functions.packageProvider
import dev.kordex.gradle.plugins.kordex.functions.versionsProvider
import dev.kordex.gradle.plugins.kordex.helpers.I18nHelper
import dev.kordex.gradle.plugins.kordex.helpers.KspPluginHelper
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
	val problems: Problems,
	val dependencies: DependencyFactory,
	val providers: ProviderFactory,
) : Plugin<Project> {
	@Suppress("UnnecessaryParentheses")
	override fun apply(target: Project) {
		val extension = target.extensions.create<KordExExtension>("kordEx").apply {
			setup()
		}

		val versionsProvider = providers.versionsProvider(extension)
		val packageProvider = providers.packageProvider(versionsProvider)
		val configurationsProvider = providers.configurationsProvider(extension)

		KspPluginHelper.apply(target)

		target.checkTask(extension, versionsProvider, problems.reporter)

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

						extension.modules.get().normalizeModules(versions.kordEx, problems.reporter).forEach { module ->
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

					deps
				}
			)
		}

		target.afterEvaluate {
			val versions = versionsProvider.get()

			if (extension.hasBot && extension.hasPlugin) {
				problems.reporter.throwing(
					RuntimeException("Project is both a bot and a plugin"),
					ProblemIds.ProjectBothBotAndPlugin,
				) {
					details("Project ${target.name} cannot be both a bot and a plugin")
					solution("If you need both in the same project, split them into separate Gradle subprojects")
					severity(Severity.ERROR)
				}
			}

			if (extension.hasBot) {
				KordExBotHelper.process(target, extension, versions)
			}

			if (extension.hasI18n) {
				if (Version(versions.kordEx.version.replace("-SNAPSHOT", "")) < "2.3.0") {
					error(
						"The `i18n` builder is only applicable to Kord Extensions version 2.3.0 and later. " +
							"Current version: ${versions.kordEx}"
					)
				}

				I18nHelper.apply(target, extension.i18n)
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

		target.repo("KordEx (Releases, R2)", KORDEX_RELEASES) {
			content {
				includeGroupAndSubgroups("com.kotlindiscord")
				includeGroupAndSubgroups("dev.kordex")
			}
		}

		target.repo("KordEx (Snapshots, R2)", KORDEX_SNAPSHOTS) {
			content {
				includeGroupAndSubgroups("com.kotlindiscord")
				includeGroupAndSubgroups("dev.kordex")
			}
		}

		target.repo("KordEx (Mirror, Reposilite)", KORDEX_MIRROR_REPOSILITE) {
			content {
				includeGroupAndSubgroups("dev.kord")
			}
		}

		target.repo("KordEx (Mirror, R2)", KORDEX_MIRROR) {
			content {
				includeGroupAndSubgroups("dev.kord")
			}
		}

		if (extension.addThirdPartyRepositories.get()) {
			target.repo("KordEx (Third-Party, Releases)", KORDEX_EXTERNAL_RELEASES)
			target.repo("KordEx (Third-Party, Snapshots)", KORDEX_EXTERNAL_SNAPSHOTS)
		}

		val modules = extension.modules.get().normalizeModules(kordExVersion, problems.reporter, log = false)

		if (MAPPINGS_V1 in modules || MAPPINGS_V2 in modules) {
			target.repo("FabricMC", "https://maven.fabricmc.net")
			target.repo("QuiltMC (Releases)", "https://maven.quiltmc.org/repository/release")
			target.repo("QuiltMC (Snapshots)", "https://maven.quiltmc.org/repository/snapshot")
			target.repo("Shedaniel", "https://maven.shedaniel.me")
			target.repo("Jitpack", "https://jitpack.io")
		}
	}

	private fun configurePlugins(target: Project, extension: KordExExtension, kordExGradle: GradleMetadata) {
		val javaVersion = if (extension.jvmTarget.isPresent) {
			extension.jvmTarget.get()
		} else {
			// NOTE: Ordinal starts from 0, so we need to add 1 to get the right number.
			target.extensions.getByType<JavaPluginExtension>().targetCompatibility.ordinal + 1
		}

		val versionElement = kordExGradle
			.variants
			.first { it.name == "apiElements" }
			.attributes?.get("org.gradle.jvm.version")
			?: kordExGradle
				.variants
				.first { it.name == "runtimeElements" }
				.attributes?.get("org.gradle.jvm.version")

		val kordExJavaVersion = versionElement?.jsonPrimitive?.int

		if (kordExJavaVersion != null && kordExJavaVersion > javaVersion) {
			problems.reporter.throwing(
				RuntimeException("Target Java version is lower than Kord Extensions' minimum required java version"),
				ProblemIds.JavaVersionTooOld,
			) {
				details("Java version $javaVersion is lower than $kordExJavaVersion")
				solution("Configure your project to use Java $kordExJavaVersion or later")
				severity(Severity.ERROR)
			}
		}

		target.tasks.withType<KotlinCompile> {
			compilerOptions {
				optIn.add("kotlin.RequiresOptIn")

				jvmTarget.set(JvmTarget.fromTarget(javaVersion.toString()))
			}
		}

		target.extensions.configure<JavaPluginExtension> {
			sourceCompatibility = JavaVersion.toVersion(javaVersion.toString())
			targetCompatibility = JavaVersion.toVersion(javaVersion.toString())
		}
	}
}
