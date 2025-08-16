/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package test.unit

import dev.kordex.gradle.plugins.docker.Dockerfile
import dev.kordex.gradle.plugins.docker.DockerfileBuilder
import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.test.Test

class DockerfileBuilderTest {
	@Test
	fun testBuilder() {
		val builder = DockerfileBuilder()

		builder.dockerFile = Dockerfile()
		builder.file("")

		assertDoesNotThrow("Should not throw when accessing `file`") {
			builder.target
		}
	}
}
