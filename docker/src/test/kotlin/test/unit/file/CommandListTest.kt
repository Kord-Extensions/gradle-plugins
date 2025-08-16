/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package test.unit.file

import dev.kordex.gradle.plugins.docker.file.*
import dev.kordex.gradle.plugins.docker.file.commands.*
import kotlin.test.Test

class CommandListTest {
	inline fun <reified T : DockerfileCommand> CommandList.lastIs() {
		assert(this.last() is T) {
			"Last item should be ${T::class.simpleName}"
		}
	}

	@Test
	fun testExtensionFunctions() {
		val commands: CommandList = mutableListOf()

		commands.add(arrayOf(""), "")
		commands.lastIs<AddCommand>()

		commands.add(arrayOf(""), "") { }
		commands.lastIs<AddCommand>()

		commands.arg("", "")
		commands.lastIs<ArgCommand>()

		commands.cmdExec("")
		commands.lastIs<CmdCommand.Exec>()

		commands.cmdShell("")
		commands.lastIs<CmdCommand.Shell>()

		commands.comment("")
		commands.lastIs<Comment>()

		commands.copy("", "")
		commands.lastIs<CopyCommand>()

		commands.copy("", "") { }
		commands.lastIs<CopyCommand>()

		commands.copy(arrayOf(""), "")
		commands.lastIs<CopyCommand>()

		commands.copy(arrayOf(""), "") { }
		commands.lastIs<CopyCommand>()

		commands.entryPointExec("")
		commands.lastIs<EntrypointCommand.Exec>()

		commands.entryPointShell("")
		commands.lastIs<EntrypointCommand.Shell>()

		commands.emptyLine()
		commands.lastIs<EmptyLine>()

		commands.env(mapOf("" to ""))
		commands.lastIs<EnvCommand>()

		commands.env { add("", "") }
		commands.lastIs<EnvCommand>()

		commands.expose(80)
		commands.lastIs<ExposeCommand>()

		commands.from("")
		commands.lastIs<FromCommand>()

		commands.healthcheck { none() }
		commands.lastIs<HealthcheckCommand.None>()

		commands.label { label("", "") }
		commands.lastIs<LabelCommand>()

		commands.label("", "")
		commands.lastIs<LabelCommand>()

		commands.onBuild { cmdShell("") }
		commands.lastIs<OnBuildCommand>()

		commands.runExec("")
		commands.lastIs<RunCommand.Exec>()

		commands.runExec("") { }
		commands.lastIs<RunCommand.Exec>()

		commands.runShell("") { }
		commands.lastIs<RunCommand.Shell>()

		commands.shell("")
		commands.lastIs<ShellCommand>()

		commands.stopSignal("")
		commands.lastIs<StopSignalCommand>()

		commands.user("")
		commands.lastIs<UserCommand>()

		commands.volume("")
		commands.lastIs<VolumeCommand>()

		commands.workdir("")
		commands.lastIs<WorkdirCommand>()

		assert(commands.size == 29) {
			"Command list should contain 27 items"
		}
	}
}
