/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.kordex.gradle.plugins.kordex.functions

import dev.kordex.gradle.plugins.kordex.PackageContainer
import dev.kordex.gradle.plugins.kordex.VersionContainer
import dev.kordex.gradle.plugins.kordex.isKX2
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory

fun ProviderFactory.packageProvider(versionsProvider: Provider<VersionContainer>) = provider {
	val versions = versionsProvider.get()

	val basePackage = if (versions.kordEx.isKX2) {
		"dev.kordex"
	} else {
		"com.kotlindiscord.kord.extensions"
	}

	val modulePackage = if (versions.kordEx.isKX2) {
		"$basePackage.modules"
	} else {
		basePackage
	}

	PackageContainer(basePackage, modulePackage)
}
