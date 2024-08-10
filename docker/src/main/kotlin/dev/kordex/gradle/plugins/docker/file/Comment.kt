/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.kordex.gradle.plugins.docker.file

class Comment(val text: String) : DockerfileCommand() {
	override val keyword: String = "#"

	override fun toString(): String =
		text.replace("\r\n", "\n")
			.split("\n")
			.map { "$keyword $it" }
			.joinToString("\n")
}
