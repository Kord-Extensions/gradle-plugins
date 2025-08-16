/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package test.unit.commands

import dev.kordex.gradle.plugins.docker.file.commands.ArgCommand
import kotlin.test.Test

class ArgCommandTest {
	@Test
	fun testBasic() {
		// ARG test

		val command = ArgCommand("test")
		val output = command.toString()

		assert(output.startsWith("ARG ")) {
			"Command output should start with `ARG `"
		}

		assert(output == "ARG test") {
			"Command output should match in full"
		}
	}

	@Test
	fun testValue() {
		// ARG test=test

		val command = ArgCommand("test", "test")
		val output = command.toString()

		assert(output == "ARG test=test") {
			"Command output should match in full"
		}
	}
}
