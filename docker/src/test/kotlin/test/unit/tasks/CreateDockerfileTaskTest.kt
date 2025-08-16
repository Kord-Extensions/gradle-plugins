/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package test.unit.tasks

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
		kotlin("jvm") version "$KOTLIN_VERSION"

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

val BUILDSCRIPT = """
	import dev.kordex.gradle.plugins.docker.file.from
	import dev.kordex.gradle.plugins.docker.tasks.CreateDockerfileTask

	plugins {
		kotlin("jvm")

		id("dev.kordex.gradle.docker")
	}


	tasks.register<CreateDockerfileTask>("testCreateDockerfile") {
		file = rootProject.file("Dockerfile")

		dockerFile.commands {
			from("test")
		}

		action()
	}
""".trimIndent()

class CreateDockerfileTaskTest {
	@field:TempDir
	lateinit var testProjectDir: File

	val buildFile
		get() =
			testProjectDir.resolve("build.gradle.kts")

	val settingsFile
		get() =
			testProjectDir.resolve("settings.gradle.kts")

	fun runGradleTask(task: String): BuildResult =
		GradleRunner.create()
			.withProjectDir(testProjectDir)
			.withArguments(task)
			.withPluginClasspath()
			.build()

	@BeforeTest
	fun setup() {
		testProjectDir.mkdirs()
		settingsFile.writeText(SETTINGS_GRADLE)
		buildFile.writeText(BUILDSCRIPT)
	}

	@AfterTest
	fun teardown() {
		testProjectDir.deleteRecursively()
	}

	@Test
	fun testTask() {
		runGradleTask("testCreateDockerfile")
	}
}
