/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.kordex.gradle.plugins.kordex.functions

import dev.kordex.gradle.plugins.kordex.base.KordExExtension
import org.gradle.api.provider.ProviderFactory

fun ProviderFactory.configurationsProvider(extension: KordExExtension) = provider {
	if (extension.configurations.isPresent && extension.configurations.get().isNotEmpty()) {
		extension.configurations.get().toTypedArray()
	} else if (extension.hasPlugin) {
		arrayOf("compileOnly")
	} else {
		arrayOf("implementation")
	}
}
