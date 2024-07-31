/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.kordex.gradle.plugins.kordex.base

import dev.kordex.gradle.plugins.kordex.bot.KordExBotSettings
import dev.kordex.gradle.plugins.kordex.bot.setup
import dev.kordex.gradle.plugins.kordex.plugins.KordExPluginSettings
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.create

abstract class KordExExtension : ExtensionAware {
	abstract val addRepositories: Property<Boolean>
	abstract val ignoreIncompatibleKotlinVersion: Property<Boolean>

	abstract val kordExVersion: Property<String>
	abstract val kordVersion: Property<String>

	abstract val modules: ListProperty<String>

	@Suppress("VariableNaming", "PropertyName")
	internal lateinit var _bot: KordExBotSettings

	@Suppress("VariableNaming", "PropertyName")
	internal lateinit var _plugin: KordExPluginSettings

	internal val hasBot: Boolean get() = _bot.mainClass.isPresent
	internal val hasPlugin: Boolean get() = _plugin.pluginClass.isPresent

	fun module(module: String) {
		modules.add(module)
	}

	internal fun setup() {
		_bot = (this as ExtensionAware).extensions.create<KordExBotSettings>("bot")
		_plugin = (this as ExtensionAware).extensions.create<KordExPluginSettings>("plugin")

		_bot.setup()

		addRepositories.convention(true)
		ignoreIncompatibleKotlinVersion.convention(false)
	}
}
