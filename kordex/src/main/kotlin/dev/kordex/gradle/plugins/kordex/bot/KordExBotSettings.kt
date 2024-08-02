/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.kordex.gradle.plugins.kordex.bot

import dev.kordex.gradle.plugins.kordex.DataCollection
import org.gradle.api.internal.provider.PropertyFactory
import org.gradle.api.provider.Property

class KordExBotSettings(props: PropertyFactory) {
	val mainClass: Property<String> = props.property(String::class.java)
	val dataCollection: Property<DataCollection> = props.property(DataCollection::class.java)
	val voice: Property<Boolean> = props.property(Boolean::class.javaObjectType).convention(true)

	fun dataCollection(level: DataCollection?) {
		dataCollection.set(level ?: DataCollection.None)
	}
}
