/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.kordex.gradle.plugins.kordex.functions

import dev.kordex.gradle.plugins.kordex.Version
import dev.kordex.gradle.plugins.kordex.VersionContainer
import dev.kordex.gradle.plugins.kordex.base.KordExExtension
import dev.kordex.gradle.plugins.kordex.base.latestKordExMetadata
import dev.kordex.gradle.plugins.kordex.base.latestKordMetadata
import dev.kordex.gradle.plugins.kordex.resolvers.GradleMetadataResolver
import org.gradle.api.provider.ProviderFactory

fun ProviderFactory.versionsProvider(extension: KordExExtension) = provider {
	val kordExVersion = if (!extension.kordExVersion.isPresent || extension.kordExVersion.orNull == "latest") {
		latestKordExMetadata?.getCurrentVersion()
			?: error("Unable to resolve Kord Extensions release metadata. Please report this!")
	} else {
		extension.kordExVersion.map(::Version).orNull
	}!!

	val kordExGradle = GradleMetadataResolver.kordEx(kordExVersion)
		?: error("Unable to resolve Kord Extensions release metadata. Please report this!")

	val kordVersion = when (extension.kordVersion.orNull) {
		null ->
			kordExGradle.variants
				.first { it.name == "runtimeElements" }
				.dependencies
				.first { it.module == "kord-core-voice" }
				.version["requires"]
				?.let { Version(it) }

		"latest" -> latestKordMetadata?.getCurrentKordVersion()

		else -> extension.kordVersion.map(::Version).orNull
	} ?: error("Unable to resolve Kord release metadata. Please report this!")

	VersionContainer(kordExVersion, kordVersion, kordExGradle)
}
