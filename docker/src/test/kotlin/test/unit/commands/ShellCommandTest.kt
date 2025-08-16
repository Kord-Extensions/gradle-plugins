/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package test.unit.commands

import dev.kordex.gradle.plugins.docker.file.commands.ShellCommand
import kotlin.test.Test

class ShellCommandTest {
	@Test
	fun testCommand() {
		// SHELL ["one", "two"]

		val command = ShellCommand(arrayOf("one", "two"))
		val output = command.toString()

		assert(output.startsWith("SHELL ")) {
			"Command output should start with `SHELL `"
		}

		assert(output == "SHELL [ \"one\", \"two\" ]") {
			"Command output should match in full"
		}
	}
}
