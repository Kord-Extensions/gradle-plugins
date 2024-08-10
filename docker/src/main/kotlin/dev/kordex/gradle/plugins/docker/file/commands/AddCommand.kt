/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.kordex.gradle.plugins.docker.file.commands

import dev.kordex.gradle.plugins.docker.file.DockerfileCommand
import java.io.Serializable

class AddCommand(
	val sources: Array<String>,
	val target: String,
) : DockerfileCommand() {
	override val keyword: String = "ADD"

	val options: MutableList<Option<*>> = mutableListOf()

	override fun toString(): String = buildString {
		append("$keyword ")

		if (options.isNotEmpty()) {
			append(
				options.joinToString(" ") {
					"${it.string}=${it.argument}"
				}
			)

			append(" ")
		}

		append("[ ")

		append(
			sources.joinToString(", ") {
				"\"$it\""
			}
		)

		append(", \"$target\"")

		append(" ]")
	}

	fun option(option: Option<*>) {
		options.add(option)
	}

	sealed class Option<T>(val string: String, val argument: T) : Serializable {
		object KeepGitDir : Option<Boolean>("--keep-git-dir", true)

		class Checksum(checksum: String) : Option<String>("--checksum", checksum)
		class Chmod(perms: Int) : Option<Int>("--chmod", perms)
		class Chown(user: String, group: String) : Option<String>("--chown", "$user:$group")
		class Exclude(path: String) : Option<String>("--exclude", path)
		class Link : Option<Boolean>("--link", true)

		companion object {
			private const val serialVersionUID: Long = 3L
		}
	}
}
