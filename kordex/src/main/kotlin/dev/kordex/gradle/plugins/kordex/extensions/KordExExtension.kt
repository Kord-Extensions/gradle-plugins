/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.kordex.gradle.plugins.kordex.extensions

import dev.kordex.gradle.plugins.kordex.DataCollection
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property

interface KordExExtension {
	val mainClass: Property<String>

	val addRepositories: Property<Boolean>
	val dataCollection: Property<DataCollection>
	val ignoreIncompatibleKotlinVersion: Property<Boolean>

	val voice: Property<Boolean>

	val kordVersion: Property<String>
	val kordExVersion: Property<String>

	val modules: ListProperty<String>

	fun dataCollection(level: DataCollection?) {
		dataCollection.set(level ?: DataCollection.None)  // Makes linter happy.
	}

	fun module(module: String) {
		modules.add(module)
	}
}
