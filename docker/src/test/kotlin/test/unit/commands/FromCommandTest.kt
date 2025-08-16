/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package test.unit.commands

import dev.kordex.gradle.plugins.docker.file.commands.FromCommand
import kotlin.test.Test

class FromCommandTest {
	@Test
	fun testBasic() {
		// FROM test

		val command = FromCommand("test")
		val output = command.toString()

		assert(output.startsWith("FROM ")) {
			"Command output should start with `FROM `"
		}

		assert(output == "FROM test") {
			"Command output should match in full"
		}
	}

	@Test
	fun testFull() {
		// FROM --platform=platform test AS alias

		val command = FromCommand(
			"test",
			"alias",
			"platform"
		)

		val output = command.toString()

		assert(output == "FROM --platform=platform test AS alias") {
			"Command output should match in full"
		}
	}
}
