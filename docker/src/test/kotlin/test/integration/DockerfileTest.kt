/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package test.integration

import dev.kordex.gradle.plugins.docker.Dockerfile
import dev.kordex.gradle.plugins.docker.file.*
import kotlin.test.Test

class DockerfileTest {
	@Test
	fun testBasic() {
		val file = Dockerfile()

		assert(file.directives.isNotEmpty()) {
			"Should contain some default directives"
		}

		val output = file.toString()

		assert(
			output ==
				"# escape=\\\n" +
				"# syntax=docker/dockerfile:1\n" +
				"\n"
		) {
			"File output should match in full"
		}
	}

	@Test
	fun testWithContents() {
		val file = Dockerfile()

		file.commands {
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

		val output = file.toString()

		assert(
			output ==
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
}
