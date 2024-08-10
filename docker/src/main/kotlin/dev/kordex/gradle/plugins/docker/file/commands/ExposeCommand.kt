/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.kordex.gradle.plugins.docker.file.commands

import dev.kordex.gradle.plugins.docker.file.DockerfileCommand
import java.io.Serializable

class ExposeCommand(
	val port: Int,
	val protocol: Protocol = Protocol.TCP,
	val comment: String? = null
) : DockerfileCommand() {
	override val keyword: String = "EXPOSE"

	override fun toString(): String = buildString {
		append("$keyword $port/$protocol")

		if (comment != null) {
			append(" # $comment")
		}
	}

	sealed class Protocol(val protocol: String) : Serializable {
		override fun toString(): String = protocol

		object TCP : Protocol("tcp")
		object UDP : Protocol("udp")

		companion object {
			private const val serialVersionUID: Long = 5L
		}
	}
}
