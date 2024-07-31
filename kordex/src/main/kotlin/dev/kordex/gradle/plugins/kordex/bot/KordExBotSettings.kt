/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.kordex.gradle.plugins.kordex.bot

import dev.kordex.gradle.plugins.kordex.DataCollection
import org.gradle.api.provider.Property

interface KordExBotSettings {
	val mainClass: Property<String>
	val dataCollection: Property<DataCollection>
	val voice: Property<Boolean>

	fun dataCollection(level: DataCollection?) {
		dataCollection.set(level ?: DataCollection.None)
	}
}

internal fun KordExBotSettings.setup() {
	voice.convention(true)
}
