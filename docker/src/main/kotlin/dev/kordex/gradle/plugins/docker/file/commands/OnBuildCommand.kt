/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.kordex.gradle.plugins.docker.file.commands

import dev.kordex.gradle.plugins.docker.file.DockerfileCommand

class OnBuildCommand(
	val command: DockerfileCommand
) : DockerfileCommand() {
	override val keyword: String = "ONBUILD"

	override fun toString(): String {
		when (command) {
			is CopyCommand -> if (command.options.any { it is CopyCommand.Option.From }) {
				error("ONBUILD doesn't support COPY commands using --from.")
			}

			is FromCommand -> error("ONBUILD doesn't support FROM commands.")
			is OnBuildCommand -> error("ONBUILD doesn't support chaining ONBUILD commands.")
		}

		return buildString {
			append("$keyword $command")
		}
	}
}
