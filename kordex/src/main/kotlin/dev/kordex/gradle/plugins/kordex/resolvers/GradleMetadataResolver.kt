/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.kordex.gradle.plugins.kordex.resolvers

import dev.kordex.gradle.plugins.kordex.kordExReleasesUrl
import dev.kordex.gradle.plugins.kordex.kordExSnapshotUrl
import dev.kordex.gradle.plugins.kordex.kordReleasesUrl
import dev.kordex.gradle.plugins.kordex.kordSnapshotUrl
import dev.kordex.gradle.plugins.kordex.resolvers.gradle.GradleMetadata
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory

class GradleMetadataResolver {
	private val logger = LoggerFactory.getLogger("GradleMetadataResolver")
	private val mavenResolver = MavenMetadataResolver()

	private val client = HttpClient()
	private val json = Json

	private val cache: MutableMap<String, GradleMetadata> = mutableMapOf()
		@Synchronized get

	fun getMetadata(url: String): GradleMetadata = runBlocking {
		cache.getOrPut(url) {
			val data = client.get(url).body<String>()

			json.decodeFromString(data)
		}
	}

	fun kordEx(version: String) =
		if (version.endsWith("-SNAPSHOT")) {
			getKordExSnapshot(version)
		} else {
			getKordExRelease(version)
		}

	fun kord(version: String) =
		if (version.endsWith("-SNAPSHOT")) {
			getKordSnapshot(version)
		} else {
			getKordRelease(version)
		}

	fun getKordExRelease(version: String) = getMetadata(kordExReleasesUrl("$version/kord-extensions-$version.module"))
	fun getKordRelease(version: String) = getMetadata(kordReleasesUrl("$version/kord-core-$version.module"))

	fun getKordSnapshot(version: String) = runBlocking {
		val metadata = mavenResolver.getKordSnapshot(version)
		val currentVersion = metadata.versioning.snapshotVersions!!.first().value

		getMetadata(kordSnapshotUrl("$version/kord-core-$currentVersion.module"))
	}

	fun getKordExSnapshot(version: String) = runBlocking {
		val metadata = mavenResolver.getKordExSnapshot(version)
		val currentVersion = metadata.versioning.snapshotVersions!!.first().value

		getMetadata(kordExSnapshotUrl("$version/kord-extensions-$currentVersion.module"))
	}
}
