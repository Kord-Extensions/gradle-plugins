/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.kordex.gradle.plugins.kordex

import dev.kordex.gradle.plugins.kordex.resolvers.gradle.GradleMetadata

data class VersionContainer(
	val kordEx: Version,
	val kord: Version?,
	val kordExGradle: GradleMetadata,
)
