/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package test.unit.commands

import dev.kordex.gradle.plugins.docker.file.commands.AddCommand
import kotlin.test.Test

class AddCommandTest {
	@Test
	fun testBasic() {
		// ADD ["one", "two", "target"]

		val command = AddCommand(
			arrayOf("one", "two"),
			"target"
		)

		val output = command.toString()

		assert(output.startsWith("ADD ")) {
			"Command output should start with `ADD `"
		}

		assert(output.contains("[ \"one\", \"two\", \"target\" ]")) {
			"Command output should contain the source/target data"
		}

		assert(output == "ADD [ \"one\", \"two\", \"target\" ]") {
			"Command output should match in full"
		}
	}

	@Test
	fun testOptions() {
		// ADD --keep-git-dir=true --checksum=test --chmod=777 --chown=user:group
		//     --exclude=test --link=true ["one", "two", "target"]

		val command = AddCommand(
			arrayOf("one", "two"),
			"target"
		)

		command.option(AddCommand.Option.KeepGitDir())
		command.option(AddCommand.Option.Checksum("test"))
		command.option(AddCommand.Option.Chmod(777))
		command.option(AddCommand.Option.Chown("user", "group"))
		command.option(AddCommand.Option.Exclude("test"))
		command.option(AddCommand.Option.Link())

		val output = command.toString()

		assert(output.contains("--keep-git-dir=true")) {
			"Command output should contain --keep-git-dir as expected"
		}

		assert(output.contains("--checksum=test")) {
			"Command output should contain --checksum as expected"
		}

		assert(output.contains("--chmod=777")) {
			"Command output should contain --chmod as expected"
		}

		assert(output.contains("--chown=user:group")) {
			"Command output should contain --chmod as expected"
		}

		assert(output.contains("--exclude=test")) {
			"Command output should contain --exclude as expected"
		}

		assert(output.contains("--link=true")) {
			"Command output should contain --link as expected"
		}

		assert(
			output ==
				"ADD " +
				"--keep-git-dir=true " +
				"--checksum=test " +
				"--chmod=777 " +
				"--chown=user:group " +
				"--exclude=test " +
				"--link=true " +
				"[ \"one\", \"two\", \"target\" ]"
		) {
			"Command output should match in full"
		}
	}
}
