/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.kordex.gradle.plugins.docker.file.commands

import dev.kordex.gradle.plugins.docker.file.DockerfileCommand
import java.io.Serializable

abstract class HealthcheckCommand : DockerfileCommand() {
	class Check : HealthcheckCommand() {
		override val keyword: String = "HEALTHCHECK"

		lateinit var command: CmdCommand

		val options: MutableList<Option<*>> = mutableListOf()

		override fun toString(): String = buildString {
			append("$keyword ")

			if (options.isNotEmpty()) {
				append(
					options.joinToString(" ") {
						"${it.string}=${it.argument}"
					}
				)

				append("\\\n\t")
			}

			append(command)
		}

		fun cmdExec(vararg instructions: String) {
			command = CmdCommand.Exec(instructions)
		}

		fun cmdShell(instructions: String) {
			command = CmdCommand.Shell(instructions)
		}

		fun option(option: Option<*>) {
			options.add(option)
		}

		sealed class Option<T>(val string: String, val argument: T) : Serializable {
			class Interval(duration: String) : Option<String>("--interval", duration)
			class Timeout(duration: String) : Option<String>("--timeout", duration)
			class StartPeriod(duration: String) : Option<String>("--start-period", duration)
			class StartInterval(duration: String) : Option<String>("--start-interval", duration)
			class Retries(number: Int) : Option<Int>("--retries", number)

			companion object {
				private const val serialVersionUID: Long = 6L
			}
		}
	}

	class None : HealthcheckCommand() {
		override val keyword: String = "HEALTHCHECK"

		override fun toString(): String = buildString {
			append("$keyword NONE")
		}
	}

	class Builder {
		internal lateinit var command: HealthcheckCommand

		fun check(body: (Check).() -> Unit) {
			val newCommand = Check()

			body(newCommand)

			command = newCommand
		}

		fun none() {
			command = None()
		}

		fun build(): HealthcheckCommand =
			command
	}
}
