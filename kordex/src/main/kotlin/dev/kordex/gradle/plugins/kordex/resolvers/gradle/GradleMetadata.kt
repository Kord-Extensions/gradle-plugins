/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.kordex.gradle.plugins.kordex.resolvers.gradle

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import dev.kordex.gradle.plugins.kordex.Version as KXVersion

@Serializable
data class GradleMetadata(
	val formatVersion: String,

	val component: Component,
	val createdBy: Map<String, Map<String, String>> = mapOf(),
	val variants: List<Variant>,
) {
	@Serializable
	data class Component(
		val group: String,
		val module: String,
		val version: String,
		val attributes: Map<String, String> = mapOf()
	) {
		fun version(): KXVersion = KXVersion(version)
	}

	@Serializable
	data class Variant(
		val name: String,
		val attributes: JsonObject? = null,  // Mixed value types!

		val dependencies: List<Dependency>,
		val files: List<File>,

	) {
		@Serializable
		data class Dependency(
			val group: String,
			val module: String,
			val version: Map<String, String> = mapOf()
		)

		@Serializable
		data class File(
			val name: String,
			val url: String,
			val size: Long,
			val sha512: String,
			val sha256: String,
			val sha1: String,
			val md5: String,
		)
	}
}
