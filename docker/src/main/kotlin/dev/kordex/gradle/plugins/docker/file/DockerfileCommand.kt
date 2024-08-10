/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.kordex.gradle.plugins.docker.file

import java.io.Serializable

abstract class DockerfileCommand : Serializable {
	abstract val keyword: String

	abstract override fun toString(): String

	companion object {
		private const val serialVersionUID: Long = 2L
	}
}
