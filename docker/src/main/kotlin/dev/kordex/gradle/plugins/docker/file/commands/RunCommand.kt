/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.kordex.gradle.plugins.docker.file.commands

import dev.kordex.gradle.plugins.docker.file.DockerfileCommand
import org.gradle.internal.impldep.org.apache.http.client.methods.RequestBuilder.options
import java.io.Serializable

@Suppress("StringLiteralDuplication")
abstract class RunCommand : DockerfileCommand() {
	var mount: MountBuilder? = null

	val options: MutableList<Option<*>> = mutableListOf()

	fun bindMount(body: MountBuilder.Bind.() -> Unit) {
		val builder = MountBuilder.Bind()

		body(builder)

		mount = builder
	}

	fun cacheMount(body: MountBuilder.Cache.() -> Unit) {
		val builder = MountBuilder.Cache()

		body(builder)

		mount = builder
	}

	fun tmpfsMount(body: MountBuilder.TmpFS.() -> Unit) {
		val builder = MountBuilder.TmpFS()

		body(builder)

		mount = builder
	}

	fun secretMount(body: MountBuilder.Secret.() -> Unit) {
		val builder = MountBuilder.Secret()

		body(builder)

		mount = builder
	}

	fun sshMount(body: MountBuilder.Ssh.() -> Unit) {
		val builder = MountBuilder.Ssh()

		body(builder)

		mount = builder
	}

	fun networkType(type: NetworkType) {
		options.add(Option.Network(type))
	}

	fun securityType(type: SecurityType) {
		options.add(Option.Security(type))
	}

	class Exec(val command: Array<out String>) : RunCommand() {
		override val keyword: String = "RUN"

		override fun toString(): String = buildString {
			append("$keyword ")

			if (mount != null) {
				append("--mount=$mount \\\n    ")
			}

			if (options.isNotEmpty()) {
				append(
					options.joinToString(" \\\n    ") {
						"${it.string}=${it.argument}"
					}
				)

				append("\\\n    ")
			}

			append("[ ")

			append(
				command.joinToString(", ") {
					"\"$it\""
				}.trim()
			)

			append(" ]")
		}
	}

	class Shell(val command: String) : RunCommand() {
		override val keyword: String = "RUN"

		override fun toString(): String = buildString {
			append("$keyword ")

			if (mount != null) {
				append("--mount=$mount \\\n    ")
			}

			if (options.isNotEmpty()) {
				append(
					options.joinToString(" \\\n    ") {
						"${it.string}=${it.argument}"
					}
				)

				append("\\\n    ")
			}

			val trimmedCommand = command.trim()

			if (trimmedCommand.contains("\n")) {
				appendLine("<<EOF")
				appendLine(trimmedCommand)
				appendLine("EOF")
			} else {
				append(trimmedCommand)
			}
		}
	}

	sealed class MountBuilder : Serializable {
		abstract val type: String
		abstract override fun toString(): String

		class Bind : MountBuilder() {
			override val type: String = "bind"

			lateinit var from: String
			lateinit var target: String

			var readWrite: Boolean = false
			var source: String? = null

			override fun toString(): String = buildString {
				append("type=$type,")
				append("from=$from,")
				append("target=$target,")
				append("source=${source ?: from}")

				if (readWrite) {
					append(",rw=true")
				}
			}
		}

		class Cache : MountBuilder() {
			override val type: String = "cache"

			lateinit var from: String
			lateinit var target: String

			var gid: Int = 0
			var id: String? = null
			var mode: String = "0755"
			var readOnly: Boolean = false
			var sharing: Sharing = Sharing.Shared
			var source: String? = null
			var uid: Int = 0

			override fun toString(): String = buildString {
				append("type=$type,")
				append("id=${id ?: target},")
				append("target=$target,")

				if (readOnly) {
					append("ro=true,")
				}

				append("sharing=$sharing,")
				append("from=$from,")

				if (source != null) {
					append("source=$source,")
				}

				append("mode=$mode,")
				append("uid=$uid,")
				append("gid=$gid")
			}

			sealed class Sharing(val type: String) {
				override fun toString(): String = type

				object Shared : Sharing("shared")
				object Private : Sharing("private")
				object Locked : Sharing("locked")
			}
		}

		class TmpFS : MountBuilder() {
			override val type: String = "tmpfs"

			lateinit var size: String
			lateinit var target: String

			override fun toString(): String = buildString {
				append("type=$type,")
				append("target=$target,")
				append("size=$size")
			}
		}

		class Secret : MountBuilder() {
			override val type: String = "secret"

			var gid: String = "0"
			var id: String? = null
			var mode: String = "0400"
			var required: Boolean = false
			var target: String? = null
			var uid: String = "0"

			override fun toString(): String = buildString {
				append("type=$type,")

				if (id != null) {
					append("id=$id,")
				}

				if (target != null) {
					append("target=$target,")
				}

				if (required) {
					append("required=true,")
				}

				append("mode=$mode,")
				append("uid=$uid,")
				append("gid=$gid")
			}
		}

		class Ssh : MountBuilder() {
			override val type: String = "ssh"

			lateinit var target: String

			var gid: String = "0"
			var id: String? = null
			var mode: String = "0600"
			var required: Boolean = false
			var uid: String = "0"

			override fun toString(): String = buildString {
				append("type=$type,")
				append("id=$id,")
				append("target=$target,")

				if (required) {
					append("required=true,")
				}

				append("mode=$mode,")
				append("uid=$uid,")
				append("gid=$gid")
			}
		}

		companion object {
			private const val serialVersionUID: Long = 7L
		}
	}

	sealed class Option<T>(val string: String, open val argument: T) : Serializable {
		class Network(type: NetworkType) : Option<NetworkType>("--network", type)
		class Security(type: SecurityType) : Option<SecurityType>("--security", type)

		companion object {
			private const val serialVersionUID: Long = 8L
		}
	}

	sealed class NetworkType(val type: String) : Serializable {
		override fun toString(): String = type

		object Default : NetworkType("default")
		object Host : NetworkType("host")
		object None : NetworkType("none")

		companion object {
			private const val serialVersionUID: Long = 9L
		}
	}

	sealed class SecurityType(val type: String) : Serializable {
		override fun toString(): String = type

		object Sandbox : SecurityType("sandbox")
		object Insecure : SecurityType("insecure")

		companion object {
			private const val serialVersionUID: Long = 10L
		}
	}
}
