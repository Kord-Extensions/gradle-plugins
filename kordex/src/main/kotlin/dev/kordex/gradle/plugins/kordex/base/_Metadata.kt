/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.kordex.gradle.plugins.kordex.base

import dev.kordex.gradle.plugins.kordex.CENTRAL_BASE
import dev.kordex.gradle.plugins.kordex.compareTo
import dev.kordex.gradle.plugins.kordex.resolvers.MavenMetadataResolver

val kordReleases = MavenMetadataResolver.getKordReleases()
val kordSnapshots = MavenMetadataResolver.getKordSnapshots()
val kordExReleases = MavenMetadataResolver.getKordExReleases()
val kordExSnapshots = MavenMetadataResolver.getKordExSnapshots()

val latestKordMetadata = if (kordReleases == null && kordSnapshots == null) {
	null
} else {
	if (kordReleases == null) {
		kordSnapshots!!
	} else if (kordSnapshots == null) {
		kordReleases
	} else {
		maxOf(kordReleases, kordSnapshots) { left, right ->
			left.getCurrentVersion().compareTo(right.getCurrentVersion())
		}
	}
}

val latestKordExMetadata = if (kordExReleases == null && kordExSnapshots == null) {
	null
} else {
	if (kordExReleases == null) {
		kordExSnapshots!!
	} else if (kordExSnapshots == null) {
		kordExReleases
	} else {
		maxOf(kordExReleases, kordExSnapshots) { left, right ->
			left.getCurrentVersion().compareTo(right.getCurrentVersion())
		}
	}
}

val latestMongoDBMetadata by lazy {
	MavenMetadataResolver.getMetadata(
		"$CENTRAL_BASE/org/mongodb/mongodb-driver-kotlin-coroutine/maven-metadata.xml"
	)
}
