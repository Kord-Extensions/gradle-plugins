/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package test.unit.commands

import dev.kordex.gradle.plugins.docker.file.commands.CmdCommand
import dev.kordex.gradle.plugins.docker.file.commands.CopyCommand
import dev.kordex.gradle.plugins.docker.file.commands.FromCommand
import dev.kordex.gradle.plugins.docker.file.commands.OnBuildCommand
import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.test.Test
import kotlin.test.assertFailsWith

class OnBuildCommandTest {
	@Test
	fun testBasic() {
		// ONBUILD CMD test

		val command = OnBuildCommand(
			CmdCommand.Shell("test")
		)

		val output = command.toString()

		assert(output.startsWith("ONBUILD ")) {
			"Command output should start with `ONBUILD `"
		}

		assert(output == "ONBUILD CMD test") {
			"Command output should match in full"
		}
	}

	@Test
	fun testErrors() {
		val copyCommand = CopyCommand(arrayOf("test"), "test")
		val copyCommandFrom = CopyCommand(arrayOf("test"), "test")
		val fromCommand = FromCommand("test")
		val onBuildCommand = OnBuildCommand(CmdCommand.Shell("test"))

		copyCommandFrom.option(CopyCommand.Option.From("test"))

		assertDoesNotThrow("Command should not throw when passed a `CopyCommand` without --from") {
			OnBuildCommand(copyCommand).toString()
		}

		assertFailsWith<IllegalStateException>("Command should throw when passed a `CopyCommand` with --from") {
			OnBuildCommand(copyCommandFrom).toString()
		}

		assertFailsWith<IllegalStateException>("Command should throw when passed a `FromCommand`") {
			OnBuildCommand(fromCommand).toString()
		}

		assertFailsWith<IllegalStateException>("Command should throw when passed an `OnBuildCommand`") {
			OnBuildCommand(onBuildCommand).toString()
		}
	}
}
