/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.kordex.gradle.plugins.docker.file.commands

import dev.kordex.gradle.plugins.docker.file.DockerfileCommand

class EnvCommand(val variables: Map<String, String>) : DockerfileCommand() {
	override val keyword: String = "ENV"

	override fun toString(): String = buildString {
		append("$keyword ")

		variables.toList().joinToString(" ") { (key, value) ->
			"$key=\"${value.replace("\"", "\\\"")}\""
		}
	}
}
