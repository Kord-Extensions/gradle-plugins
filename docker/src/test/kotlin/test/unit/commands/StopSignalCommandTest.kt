/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package test.unit.commands

import dev.kordex.gradle.plugins.docker.file.commands.StopSignalCommand
import kotlin.test.Test

class StopSignalCommandTest {
	@Test
	fun testCommand() {
		// STOPSIGNAL signal

		val command = StopSignalCommand("signal")
		val output = command.toString()

		assert(output.startsWith("STOPSIGNAL ")) {
			"Command output should start with `STOPSIGNAL `"
		}

		assert(output == "STOPSIGNAL signal") {
			"Command output should match in full"
		}
	}
}
