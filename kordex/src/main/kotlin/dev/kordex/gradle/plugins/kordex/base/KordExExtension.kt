/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.kordex.gradle.plugins.kordex.base

import dev.kordex.gradle.plugins.kordex.bot.KordExBotSettings
import dev.kordex.gradle.plugins.kordex.plugins.KordExPluginSettings
import org.gradle.api.Action
import org.gradle.api.internal.provider.PropertyFactory
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import javax.inject.Inject

abstract class KordExExtension @Inject constructor(props: PropertyFactory) : ExtensionAware {
	abstract val addRepositories: Property<Boolean>
	abstract val configurations: ListProperty<String>
	abstract val ignoreIncompatibleKotlinVersion: Property<Boolean>
	abstract val jvmTarget: Property<Int>

	abstract val kordExVersion: Property<String>
	abstract val kordVersion: Property<String>

	abstract val modules: ListProperty<String>

	internal val bot: KordExBotSettings = KordExBotSettings(props)
	internal val plugin: KordExPluginSettings = KordExPluginSettings(props)

	internal var hasBot = false
	internal var hasPlugin = false

	fun bot(action: Action<KordExBotSettings>) {
		action.execute(bot)

		hasBot = true
	}

	fun plugin(action: Action<KordExPluginSettings>) {
		action.execute(plugin)

		hasPlugin = true
	}

	fun module(module: String) {
		modules.add(module)
	}

	internal fun setup() {
		addRepositories.convention(true)
		ignoreIncompatibleKotlinVersion.convention(false)
	}
}
