/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package test.unit.commands

import dev.kordex.gradle.plugins.docker.file.commands.UserCommand
import kotlin.test.Test

class UserCommandTest {
	@Test
	fun testCommandBasic() {
		// USER user

		val command = UserCommand("user")
		val output = command.toString()

		assert(output.startsWith("USER ")) {
			"Command output should start with `USER `"
		}

		assert(output == "USER user") {
			"Command output should match in full"
		}
	}

	@Test
	fun testCommandGroup() {
		// USER user:group

		val command = UserCommand("user", "group")
		val output = command.toString()

		assert(output == "USER user:group") {
			"Command output should match in full"
		}
	}
}
