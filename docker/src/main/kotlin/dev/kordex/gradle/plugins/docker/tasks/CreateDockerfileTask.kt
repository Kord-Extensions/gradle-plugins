/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.kordex.gradle.plugins.docker.tasks

import dev.kordex.gradle.plugins.docker.Dockerfile
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.io.File
import kotlin.io.writeText

@CacheableTask
abstract class CreateDockerfileTask : DefaultTask() {
	@get:OutputFile
	lateinit var file: File

	@get:Input
	val dockerFile: Dockerfile = Dockerfile()

	@TaskAction
	fun action() {
		file.writeText(dockerFile.toString())
	}
}
