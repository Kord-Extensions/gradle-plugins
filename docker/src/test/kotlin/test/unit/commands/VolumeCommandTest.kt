/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package test.unit.commands

import dev.kordex.gradle.plugins.docker.file.commands.VolumeCommand
import kotlin.test.Test

class VolumeCommandTest {
	@Test
	fun testCommand() {
		// VOLUME [ "one", "two" ]

		val command = VolumeCommand(arrayOf("one", "two"))
		val output = command.toString()

		assert(output.startsWith("VOLUME ")) {
			"Command output should start with `VOLUME `"
		}

		assert(output == "VOLUME [ \"one\", \"two\" ]") {
			"Command output should match in full"
		}
	}
}
