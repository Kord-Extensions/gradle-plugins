/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.kordex.gradle.plugins.docker

import java.io.File

class DockerfileBuilder {
	lateinit var target: File
	lateinit var dockerFile: Dockerfile

	fun file(path: String) {
		target = File(path)
	}
}
