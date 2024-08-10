/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.kordex.gradle.plugins.docker.file.commands

import dev.kordex.gradle.plugins.docker.file.DockerfileCommand

object EntrypointCommand {
	class Exec(val instructions: Array<out String>) : DockerfileCommand() {
		override val keyword: String = "ENTRYPOINT"

		override fun toString(): String = buildString {
			append("$keyword [ ")

			append(
				instructions.joinToString(", ") {
					"\"$it\""
				}
			)

			append(" ]")
		}
	}

	class Shell(val instructions: String) : DockerfileCommand() {
		override val keyword: String = "ENTRYPOINT"

		override fun toString(): String = buildString {
			append("$keyword $instructions")
		}
	}
}
