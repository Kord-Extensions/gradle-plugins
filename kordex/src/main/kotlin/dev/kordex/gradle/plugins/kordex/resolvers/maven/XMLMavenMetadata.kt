/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.kordex.gradle.plugins.kordex.resolvers.maven

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlValue

@Serializable
@SerialName("metadata")
data class XMLMavenMetadata(
	val modelVersion: String? = null,

	@XmlElement
	val artifactId: XMLArtifactId,

	@XmlElement
	val groupId: XMLGroupId,

	@XmlElement
	val versioning: XMLVersioning,

	@XmlElement
	val version: XMLVersions.XMLVersion? = null,
) {
	@Serializable
	@SerialName("versioning")
	data class XMLVersioning(
		@XmlElement
		val latest: XMLLatest? = null,

		@XmlElement
		val release: XMLRelease? = null,

		@XmlElement
		val lastUpdated: XMLLastUpdated,

		@XmlElement
		val snapshot: XmlSnapshot? = null,

		@XmlElement
		val versions: XMLVersions? = null,

		@XmlElement
		val version: XMLVersions.XMLVersion? = null,

		@XmlElement
		val snapshotVersions: XmlSnapshotVersions? = null,
	)

	@Serializable
	@SerialName("snapshot")
	data class XmlSnapshot(
		@XmlElement
		val timestamp: XmlTimestamp,

		@XmlElement
		val buildNumber: XmlBuildNumber
	) {
		@Serializable
		@SerialName("timestamp")
		data class XmlTimestamp(
			@XmlValue
			val value: String,
		)

		@Serializable
		@SerialName("buildNumber")
		data class XmlBuildNumber(
			@XmlValue
			val value: Long,
		)
	}

	@Serializable
	@SerialName("snapshotVersions")
	data class XmlSnapshotVersions(
		val value: List<XmlSnapshotVersion>
	) {
		@Serializable
		@SerialName("snapshotVersion")
		data class XmlSnapshotVersion(
			@XmlElement
			val classifier: XmlClassifier? = null,

			@XmlElement
			val extension: XmlExtension,

			@XmlElement
			val value: XmlValueElement,

			@XmlElement
			val updated: XmlUpdated
		)

		@Serializable
		@SerialName("classifier")
		data class XmlClassifier(
			@XmlValue
			val value: String
		)

		@Serializable
		@SerialName("extension")
		data class XmlExtension(
			@XmlValue
			val value: String
		)

		@Serializable
		@SerialName("value")
		data class XmlValueElement(
			@XmlValue
			val value: String
		)

		@Serializable
		@SerialName("updated")
		data class XmlUpdated(
			@XmlValue
			val value: Long
		)
	}

	@Serializable
	@SerialName("versions")
	data class XMLVersions(
		val value: List<XMLVersion>,
	) {
		@Serializable
		@SerialName("version")
		data class XMLVersion(
			@XmlValue
			val value: String,
		)
	}

	@Serializable
	@SerialName("artifactId")
	data class XMLArtifactId(
		@XmlValue
		val value: String,
	)

	@Serializable
	@SerialName("groupId")
	data class XMLGroupId(
		@XmlValue
		val value: String,
	)

	@Serializable
	@SerialName("release")
	data class XMLRelease(
		@XmlValue
		val value: String,
	)

	@Serializable
	@SerialName("latest")
	data class XMLLatest(
		@XmlValue
		val value: String,
	)

	@Serializable
	@SerialName("lastUpdated")
	data class XMLLastUpdated(
		@XmlValue
		val value: String,
	)
}
