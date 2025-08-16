/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package test.functional

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.io.TempDir
import java.io.File
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

const val KOTLIN_VERSION = "2.1.21"

val SETTINGS_GRADLE = """
pluginManagement {
	plugins {
		kotlin("jvm") version $KOTLIN_VERSION

		id("dev.kordex.gradle.docker")
	}

    repositories {
        google()
        gradlePluginPortal()

	    maven("https://releases-repo.kordex.dev")
	    maven("https://snapshots-repo.kordex.dev")

	    mavenCentral()
        mavenLocal()
    }
}
""".trimIndent()

fun buildScript(generateOnBuild: Boolean) = """
	import dev.kordex.gradle.plugins.docker.file.*

	plugins {
		kotlin("jvm")

		id("dev.kordex.gradle.docker")
	}

	docker {
		file(rootProject.file("Dockerfile"))

		generateOnBuild = $generateOnBuild

		commands {
			from("openjdk:21-jdk-slim")
			emptyLine()

			comment("Create required directories")
			runShell("mkdir -p /bot/plugins")
			emptyLine()

			comment("Declare required volumes")
			volume("/bot/plugins")
			emptyLine()

			comment("Set the correct working directory")
			workdir("/bot")
		}
	}
""".trimIndent()

class FunctionalTest {
	@field:TempDir
	lateinit var testProjectDir: File

	val buildFile
		get() =
			testProjectDir.resolve("build.gradle.kts")

	val settingsFile
		get() =
			testProjectDir.resolve("settings.gradle.kts")

	val dockerfile
		get() =
			testProjectDir.resolve("Dockerfile")

	fun runGradleTask(task: String): BuildResult =
		GradleRunner.create()
			.withProjectDir(testProjectDir)
			.withArguments(task)
			.withPluginClasspath()
			.build()

	fun checkDockerfile() {
		val contents = dockerfile.readText()

		assert(
			contents ==
				"# escape=\\\n" +
				"# syntax=docker/dockerfile:1\n" +
				"\n" +
				"FROM openjdk:21-jdk-slim\n" +
				"\n" +
				"# Create required directories\n" +
				"RUN mkdir -p /bot/plugins\n" +
				"\n" +
				"# Declare required volumes\n" +
				"VOLUME [ \"/bot/plugins\" ]\n" +
				"\n" +
				"# Set the correct working directory\n" +
				"WORKDIR /bot\n"
		) {
			"File output should match in full"
		}
	}

	@BeforeTest
	fun setup() {
		testProjectDir.mkdirs()
		settingsFile.writeText(SETTINGS_GRADLE)
	}

	@AfterTest
	fun teardown() {
		testProjectDir.deleteRecursively()
	}

	@Test
	fun testBasic() {
		buildFile.writeText(buildScript(false))

		runGradleTask("createDockerfile")
		checkDockerfile()
	}

	@Test
	fun testGenerateOnBuild() {
		buildFile.writeText(buildScript(true))

		runGradleTask("build")
		checkDockerfile()
	}
}
