/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.kordex.gradle.plugins.docker

import dev.kordex.gradle.plugins.docker.file.CommandList
import java.io.Serializable

class Dockerfile : Serializable {
	val directives: MutableMap<String, String> = mutableMapOf(
		"escape" to "\\",
		"syntax" to "docker/dockerfile:1"
	)

	val commands: CommandList = mutableListOf()

	fun commands(body: CommandList.() -> Unit) {
		body(commands)
	}

	override fun toString(): String = buildString {
		directives.forEach { (key, value) ->
			appendLine("# $key=$value")
		}

		appendLine()

		commands.forEach(::appendLine)
	}

	companion object {
		private const val serialVersionUID: Long = 1L
	}
}
