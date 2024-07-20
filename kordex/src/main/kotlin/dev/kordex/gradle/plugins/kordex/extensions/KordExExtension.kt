/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.kordex.gradle.plugins.kordex.extensions

import dev.kordex.gradle.plugins.kordex.DataCollection

open class KordExExtension {
	var addRepositories: Boolean = true
	var dataCollection: DataCollection = DataCollection.Standard
	var ignoreIncompatibleKotlinVersion: Boolean = false

	var voice: Boolean = true

	var kordVersion: String? = null
	var kordExVersion: String? = null

	val modules: MutableList<String> = mutableListOf()

	fun dataCollection(level: DataCollection?) {
		dataCollection = level ?: DataCollection.None
	}

	fun module(module: String) {
		modules.add(module)
	}
}
