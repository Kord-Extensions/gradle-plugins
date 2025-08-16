/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package test.unit.commands

import dev.kordex.gradle.plugins.docker.file.commands.LabelCommand
import kotlin.test.Test

class LabelCommandTest {
	@Test
	fun testCommand() {
		// LABEL "one"="value1" "two"="value2" "three"="\"value3\"" "\"four\""="value4"

		val command = LabelCommand()

		command.label("one", "value1")
		command.label("two", "value2")
		command.label("three", "\"value3\"")
		command.label("\"four\"", "value4")

		val output = command.toString()

		assert(output.startsWith("LABEL ")) {
			"Command output should start with `LABEL `"
		}

		assert(
			output ==
				"LABEL " +
				"\"one\"=\"value1\" " +
				"\"two\"=\"value2\" " +
				"\"three\"=\"\\\"value3\\\"\" " +
				"\"\\\"four\\\"\"=\"value4\""
		) {
			"Command output should match in full"
		}
	}
}
