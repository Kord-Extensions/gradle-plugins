/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.kordex.gradle.plugins.kordex.resolvers.maven

import org.gradle.kotlin.dsl.closureOf

class MavenMetadata(private val xml: XMLMavenMetadata) {
	val artifactId: String get() = xml.artifactId.value
	val groupId: String get() = xml.groupId.value
	val version: String? get() = xml.version?.value

	val versioning: Versioning by lazy { Versioning(xml.versioning) }

	override fun toString(): String =
		"MavenMetadata(artifactId=\"$artifactId\", groupId=\"$groupId\", versioning=\"$versioning\")"

	class Versioning(private val xml: XMLMavenMetadata.XMLVersioning) {
		val lastUpdated: String get() = xml.lastUpdated.value
		val latest: String? get() = xml.latest?.value
		val release: String? get() = xml.release?.value
		val version: String? get() = xml.version?.value

		val snapshot by lazy {
			xml.snapshot?.let { Snapshot(it) }
		}

		val snapshotVersions: List<SnapshotVersion>? by lazy {
			xml.snapshotVersions?.value?.map { SnapshotVersion(it) }
		}

		val versions: List<String>? by lazy {
			xml.versions?.value?.map { it.value }
		}

		override fun toString(): String =
			"Versioning(lastUpdated=\"$lastUpdated\", latest=\"$latest\", versions=\"$versions\")"

		class Snapshot(private val xml: XMLMavenMetadata.XmlSnapshot) {
			val timestamp: String = xml.timestamp.value
			val buildNumber: Long = xml.buildNumber.value
		}

		class SnapshotVersion(private val xml: XMLMavenMetadata.XmlSnapshotVersions.XmlSnapshotVersion) {
			val classifier: String? get() = xml.classifier?.value
			val extension: String get() = xml.extension.value
			val value: String get() = xml.value.value
			val updated: Long get() = xml.updated.value
		}
	}
}
