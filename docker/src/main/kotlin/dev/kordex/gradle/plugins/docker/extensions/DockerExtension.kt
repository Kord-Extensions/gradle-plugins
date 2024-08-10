/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.kordex.gradle.plugins.docker.extensions

import dev.kordex.gradle.plugins.docker.file.CommandList
import java.io.File
import kotlin.collections.set

open class DockerExtension {
	lateinit var target: File
	lateinit var commandsBuilder: CommandList.() -> Unit

	var generateOnBuild: Boolean = true

	val directives: MutableMap<String, String> = mutableMapOf()

	fun commands(body: CommandList.() -> Unit) {
		commandsBuilder = body
	}

	fun directive(key: String, value: String) {
		directives[key] = value
	}

	fun file(input: File) {
		target = input
	}
}
