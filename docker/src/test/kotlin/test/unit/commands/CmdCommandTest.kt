/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package test.unit.commands

import dev.kordex.gradle.plugins.docker.file.commands.CmdCommand
import kotlin.test.Test

class CmdCommandTest {
	@Test
	fun testExec() {
		// CMD ["one", "two"]

		val command = CmdCommand.Exec(arrayOf("one", "two"))
		val output = command.toString()

		assert(output.startsWith("CMD ")) {
			"Command output should start with `CMD `"
		}

		assert(output == "CMD [ \"one\", \"two\" ]") {
			"Command output should match in full"
		}
	}

	@Test
	fun testShell() {
		// CMD one two

		val command = CmdCommand.Shell("one two")
		val output = command.toString()

		assert(output.startsWith("CMD ")) {
			"Command output should start with `CMD `"
		}

		assert(output == "CMD one two") {
			"Command output should match in full"
		}
	}
}
