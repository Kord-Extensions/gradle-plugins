/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package test.unit.extensions

import dev.kordex.gradle.plugins.docker.extensions.DockerExtension
import org.junit.jupiter.api.assertDoesNotThrow
import java.io.File
import kotlin.test.Test

class TestDockerExtension {
	@Test
	fun testExtension() {
		val extension = DockerExtension()

		assert(extension.generateOnBuild) {
			"`generateOnBuild` should be true by default"
		}

		extension.commands {}

		assertDoesNotThrow("Should not throw when calling empty `commandsBuilder`") {
			extension.commandsBuilder.invoke(mutableListOf())
		}

		extension.file(File(""))

		assertDoesNotThrow("Should not throw when accessing `target`") {
			extension.target
		}

		extension.directive("key", "value")

		assert(extension.directives["key"] == "value") {
			"Directives should contain `key` -> `value`"
		}
	}
}
