/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package test.unit.file

import dev.kordex.gradle.plugins.docker.file.EmptyLine
import kotlin.test.Test

class EmptyLineTest {
	@Test
	fun testCommand() {
		val comment = EmptyLine()
		val output = comment.toString()

		assert(output == "") {
			"Comment output should match in full"
		}
	}
}
