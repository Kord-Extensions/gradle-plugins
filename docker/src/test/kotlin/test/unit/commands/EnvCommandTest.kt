/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package test.unit.commands

import dev.kordex.gradle.plugins.docker.file.commands.EnvCommand
import kotlin.test.Test

class EnvCommandTest {
	@Test
	fun testCommand() {
		// ENV one="value1" two="value2" three="\"value3\""

		val command = EnvCommand(
			mapOf(
				"one" to "value1",
				"two" to "value2",
				"three" to "\"value3\"",
			)
		)

		val output = command.toString()

		assert(output.startsWith("ENV ")) {
			"Command output should start with `ENV `"
		}

		assert(output == "ENV one=\"value1\" two=\"value2\" three=\"\\\"value3\\\"\"") {
			"Command output should match in full"
		}
	}
}
