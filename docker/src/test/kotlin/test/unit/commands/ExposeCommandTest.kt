/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package test.unit.commands

import dev.kordex.gradle.plugins.docker.file.commands.ExposeCommand
import kotlin.test.Test

class ExposeCommandTest {
	@Test
	fun testBasic() {
		// EXPOSE 80/tcp

		val command = ExposeCommand(80)
		val output = command.toString()

		assert(output.startsWith("EXPOSE ")) {
			"Command output should start with `EXPOSE `"
		}

		assert(output == "EXPOSE 80/tcp") {
			"Command output should match in full"
		}
	}

	@Test
	fun testTCP() {
		// EXPOSE 80/tcp # test comment

		val command = ExposeCommand(
			80,
			ExposeCommand.Protocol.TCP,
			"test comment"
		)

		val output = command.toString()

		assert(output == "EXPOSE 80/tcp # test comment") {
			"Command output should match in full"
		}
	}

	@Test
	fun testUDP() {
		// EXPOSE 80/udp # test comment

		val command = ExposeCommand(
			80,
			ExposeCommand.Protocol.UDP,
			"test comment"
		)

		val output = command.toString()

		assert(output == "EXPOSE 80/udp # test comment") {
			"Command output should match in full"
		}
	}
}
