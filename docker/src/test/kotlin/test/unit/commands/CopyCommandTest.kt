/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package test.unit.commands

import dev.kordex.gradle.plugins.docker.file.commands.CopyCommand
import kotlin.test.Test

class CopyCommandTest {
	@Test
	fun testBasic() {
		// COPY ["one", "two", "target"]

		val command = CopyCommand(
			arrayOf("one", "two"),
			"target"
		)

		val output = command.toString()

		assert(output.startsWith("COPY ")) {
			"Command output should start with `COPY `"
		}

		assert(output == "COPY [ \"one\", \"two\", \"target\" ]") {
			"Command output should match in full"
		}
	}

	@Test
	fun testOptions() {
		// COPY --chmod=777 --chown=user:group --exclude=test --from=test --link=true
		//      --parents=true ["one", "two", "target"]

		val command = CopyCommand(
			arrayOf("one", "two"),
			"target"
		)

		command.option(CopyCommand.Option.Chmod(777))
		command.option(CopyCommand.Option.Chown("user", "group"))
		command.option(CopyCommand.Option.Exclude("test"))
		command.option(CopyCommand.Option.From("test"))
		command.option(CopyCommand.Option.Link())
		command.option(CopyCommand.Option.Parents())

		val output = command.toString()

		assert(output.contains("--chmod=777")) {
			"Command output should contain --chmod as expected"
		}

		assert(output.contains("--chown=user:group")) {
			"Command output should contain --chmod as expected"
		}

		assert(output.contains("--exclude=test")) {
			"Command output should contain --exclude as expected"
		}

		assert(output.contains("--from=test")) {
			"Command output should contain --from as expected"
		}

		assert(output.contains("--link=true")) {
			"Command output should contain --link as expected"
		}

		assert(output.contains("--parents=true")) {
			"Command output should contain --parents as expected"
		}

		assert(
			output ==
				"COPY " +
				"--chmod=777 " +
				"--chown=user:group " +
				"--exclude=test " +
				"--from=test " +
				"--link=true " +
				"--parents=true " +
				"[ \"one\", \"two\", \"target\" ]"
		) {
			"Command output should match in full"
		}
	}
}
