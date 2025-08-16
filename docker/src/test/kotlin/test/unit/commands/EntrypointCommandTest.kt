/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package test.unit.commands

import dev.kordex.gradle.plugins.docker.file.commands.EntrypointCommand
import kotlin.test.Test

class EntrypointCommandTest {
	@Test
	fun testExec() {
		// ENTRYPOINT ["one", "two"]

		val command = EntrypointCommand.Exec(arrayOf("one", "two"))
		val output = command.toString()

		assert(output.startsWith("ENTRYPOINT ")) {
			"Command output should start with `ENTRYPOINT `"
		}

		assert(output == "ENTRYPOINT [ \"one\", \"two\" ]") {
			"Command output should match in full"
		}
	}

	@Test
	fun testShell() {
		// ENTRYPOINT one two

		val command = EntrypointCommand.Shell("one two")
		val output = command.toString()

		assert(output.startsWith("ENTRYPOINT ")) {
			"Command output should start with `ENTRYPOINT `"
		}

		assert(output == "ENTRYPOINT one two") {
			"Command output should match in full"
		}
	}
}
