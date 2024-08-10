/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.kordex.gradle.plugins.docker.file.commands

import dev.kordex.gradle.plugins.docker.file.DockerfileCommand

class ArgCommand(
	val key: String,
	val value: String? = null,
) : DockerfileCommand() {
	override val keyword: String = "ARG"

	override fun toString(): String = buildString {
		append("$keyword $key")

		if (value != null) {
			append("=$value")
		}
	}
}
