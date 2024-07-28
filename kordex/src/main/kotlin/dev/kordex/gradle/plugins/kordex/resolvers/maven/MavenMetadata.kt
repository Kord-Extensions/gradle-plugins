/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.kordex.gradle.plugins.kordex.resolvers.maven

import dev.kordex.gradle.plugins.kordex.Version
import dev.kordex.gradle.plugins.kordex.compareTo

class MavenMetadata(private val xml: XMLMavenMetadata) {
	val artifactId: String get() = xml.artifactId.value
	val groupId: String get() = xml.groupId.value
	val version: Version? get() = xml.version?.value?.let { Version(it) }

	val versioning: Versioning by lazy { Versioning(xml.versioning) }

	fun getCurrentVersion(): Version? {
		val versions = versioning.snapshotVersions?.map { it.value }
			?: versioning.versions
			?: return null

		return versions.maxWithOrNull(Version::compareTo)
	}

	override fun toString(): String =
		"MavenMetadata(artifactId=\"$artifactId\", groupId=\"$groupId\", versioning=\"$versioning\")"

	class Versioning(private val xml: XMLMavenMetadata.XMLVersioning) {
		val lastUpdated: String get() = xml.lastUpdated.value
		val latest: Version? get() = xml.latest?.value?.let { Version(it) }
		val release: Version? get() = xml.release?.value?.let { Version(it) }
		val version: Version? get() = xml.version?.value?.let { Version(it) }

		val snapshot by lazy {
			xml.snapshot?.let { Snapshot(it) }
		}

		val snapshotVersions: List<SnapshotVersion>? by lazy {
			xml.snapshotVersions?.value?.map { SnapshotVersion(it) }
		}

		val versions: List<Version>? by lazy {
			xml.versions?.value?.map { Version(it.value) }
		}

		override fun toString(): String =
			"Versioning(lastUpdated=\"$lastUpdated\", latest=\"$latest\", versions=\"$versions\")"

		class Snapshot(xml: XMLMavenMetadata.XmlSnapshot) {
			val timestamp: String = xml.timestamp.value
			val buildNumber: Long = xml.buildNumber.value
		}

		data class SnapshotVersion(private val xml: XMLMavenMetadata.XmlSnapshotVersions.XmlSnapshotVersion) {
			val classifier: String? get() = xml.classifier?.value
			val extension: String get() = xml.extension.value
			val value: Version get() = Version(xml.value.value)
			val updated: Long get() = xml.updated.value
		}
	}
}
