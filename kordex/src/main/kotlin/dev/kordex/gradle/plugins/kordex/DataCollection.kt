/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.kordex.gradle.plugins.kordex

import java.io.Serializable

@Suppress("UnusedPrivateMember")
sealed class DataCollection(val readable: String) : Serializable {
	object None : DataCollection("none") {
		private fun readResolve(): Any = None
	}

	object Minimal : DataCollection("minimal") {
		private fun readResolve(): Any = Minimal
	}

	object Standard : DataCollection("standard") {
		private fun readResolve(): Any = Standard
	}

	object Extra : DataCollection("extra") {
		private fun readResolve(): Any = Extra
	}

	companion object {
		@Suppress("MagicNumber")
		private const val serialVersionUID: Long = 100L
	}
}
