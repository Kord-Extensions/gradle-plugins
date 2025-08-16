/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package test.unit.commands

import dev.kordex.gradle.plugins.docker.file.commands.WorkdirCommand
import kotlin.test.Test

class WorkdirCommandTest {
	@Test
	fun testCommand() {
		// WORKDIR dir

		val command = WorkdirCommand("dir")
		val output = command.toString()

		assert(output.startsWith("WORKDIR ")) {
			"Command output should start with `WORKDIR `"
		}

		assert(output == "WORKDIR dir") {
			"Command output should match in full"
		}
	}
}
