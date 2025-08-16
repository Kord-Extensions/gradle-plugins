/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package test.unit.commands

import dev.kordex.gradle.plugins.docker.file.commands.HealthcheckCommand
import kotlin.test.Test

class HealthcheckCommandTest {
	@Test
	fun testBuilder() {
		val checkBuilder = HealthcheckCommand.Builder()
		checkBuilder.check {}

		assert(checkBuilder.build() is HealthcheckCommand.Check) {
			"`builder.check {}` should return a HealthcheckCommand.Check object"
		}

		val noneBuilder = HealthcheckCommand.Builder()
		noneBuilder.none()

		assert(noneBuilder.build() is HealthcheckCommand.None) {
			"`builder.none()` should return a HealthcheckCommand.None object"
		}
	}

	@Test
	fun testCheckBasic() {
		// HEALTHCHECK CMD test

		val command = HealthcheckCommand.Check()

		command.cmdShell("test")

		val output = command.toString()

		assert(output.startsWith("HEALTHCHECK ")) {
			"Command output should start with `HEALTHCHECK `"
		}

		assert(output == "HEALTHCHECK CMD test") {
			"Command output should match in full"
		}
	}

	@Test
	fun testCheckOptions() {
		// HEALTHCHECK --interval=duration --timeout=timeout --start-period=startPeriod
		//             --start-interval=startInterval --retries=3
		//             CMD test

		val command = HealthcheckCommand.Check()

		command.cmdShell("test")

		command.option(HealthcheckCommand.Check.Option.Interval("duration"))
		command.option(HealthcheckCommand.Check.Option.Timeout("timeout"))
		command.option(HealthcheckCommand.Check.Option.StartPeriod("startPeriod"))
		command.option(HealthcheckCommand.Check.Option.StartInterval("startInterval"))
		command.option(HealthcheckCommand.Check.Option.Retries(3))

		val output = command.toString()

		assert(output.startsWith("HEALTHCHECK ")) {
			"Command output should start with `HEALTHCHECK `"
		}

		assert(output.contains("--interval=duration")) {
			"Command output should contain --interval as expected"
		}

		assert(output.contains("--timeout=timeout")) {
			"Command output should contain --timeout as expected"
		}

		assert(output.contains("--start-period=startPeriod")) {
			"Command output should contain --start-period as expected"
		}

		assert(output.contains("--start-interval=startInterval")) {
			"Command output should contain --start-interval as expected"
		}

		assert(output.contains("--retries=3")) {
			"Command output should contain --retries as expected"
		}

		assert(
			output ==
				"HEALTHCHECK " +
				"--interval=duration " +
				"--timeout=timeout " +
				"--start-period=startPeriod " +
				"--start-interval=startInterval " +
				"--retries=3" +
				"\\\n\t" +
				"CMD test"
		) {
			"Command output should match in full"
		}
	}

	@Test
	fun testNone() {
		// HEALTHCHECK NONE

		val command = HealthcheckCommand.None()
		val output = command.toString()

		assert(output.startsWith("HEALTHCHECK ")) {
			"Command output should start with `HEALTHCHECK `"
		}

		assert(output == "HEALTHCHECK NONE") {
			"Command output should match in full"
		}
	}
}
