/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package test.unit.file

import dev.kordex.gradle.plugins.docker.file.Comment
import kotlin.test.Test

class CommentTest {
	@Test
	fun testBasic() {
		val comment = Comment("test")
		val output = comment.toString()

		assert(output == "# test") {
			"Comment output should match in full"
		}
	}

	@Test
	fun testFormatted() {
		val comment = Comment("test\r\ntest\ntest")
		val output = comment.toString()

		assert(output == "# test\n# test\n# test") {
			"Comment output should match in full"
		}
	}
}
