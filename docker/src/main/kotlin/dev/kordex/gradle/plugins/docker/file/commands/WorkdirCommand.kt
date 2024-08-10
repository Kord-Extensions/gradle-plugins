/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.kordex.gradle.plugins.docker.file.commands

class WorkdirCommand(val dir: String) : CmdCommand() {
	override val keyword: String = "WORKDIR"

	override fun toString(): String = buildString {
		append("$keyword $dir")
	}
}
