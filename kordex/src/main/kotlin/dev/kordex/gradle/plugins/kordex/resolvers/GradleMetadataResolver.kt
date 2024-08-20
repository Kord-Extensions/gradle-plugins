/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.kordex.gradle.plugins.kordex.resolvers

import dev.kordex.gradle.plugins.kordex.*
import dev.kordex.gradle.plugins.kordex.resolvers.gradle.GradleMetadata
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory

object GradleMetadataResolver {
	private val logger = LoggerFactory.getLogger(GradleMetadataResolver::class.java)

	private val client = HttpClient()
	private val json = Json {
		ignoreUnknownKeys = true
	}

	private val cache: MutableMap<String, GradleMetadata?> = mutableMapOf()
		@Synchronized get

	fun getMetadata(url: String): GradleMetadata? = runBlocking {
		val value = cache.getOrPut(url) {
			val response = client.get(url)

			logger.info("{} -> HTTP {}", url, response.status.value)

			if (response.status == HttpStatusCode.NotFound) {
				null
			} else {
				val data = response.body<String>()

				json.decodeFromString(data)
			}
		}

		logger.info("{} -> {}", url, value)

		value
	}

	fun kordEx(version: Version) =
		kordEx(version.version)

	fun kordEx(version: String) =
		if (version.endsWith("-SNAPSHOT")) {
			getKordExSnapshot(version)
		} else {
			getKordExRelease(version)
		}

	fun kord(version: Version) =
		kord(version.version)

	fun kord(version: String) =
		if (version.endsWith("-SNAPSHOT")) {
			getKordSnapshot(version)
		} else {
			getKordRelease(version)
		}

	fun getKordExRelease(version: String) = if (version.startsWith("2.")) {
		getMetadata(kordExReleasesUrlv2("$version/kord-extensions-$version.module"))
	} else {
		getMetadata(kordExReleasesUrlv1("$version/kord-extensions-$version.module"))
	}

	fun getKordRelease(version: String) = getMetadata(kordReleasesUrl("$version/kord-core-$version.module"))

	fun getKordSnapshot(version: String) = runBlocking {
		val metadata = MavenMetadataResolver.getKordSnapshot(version)
		val currentVersion = metadata?.versioning?.snapshotVersions?.first()?.value
			?: error("Unable to resolve Kord snapshot release metadata. Please report this!")

		getMetadata(kordSnapshotUrl("$version/kord-core-$currentVersion.module"))
	}

	fun getKordExSnapshot(version: String) = runBlocking {
		val metadata = MavenMetadataResolver.getKordExSnapshot(version)
		val currentVersion = metadata?.versioning?.snapshotVersions?.first()?.value
			?: error("Unable to resolve Kord Extensions snapshot release metadata. Please report this!")

		if (version.startsWith("2.")) {
			getMetadata(kordExSnapshotUrlv2("$version/kord-extensions-$currentVersion.module"))
		} else {
			getMetadata(kordExSnapshotUrlv1("$version/kord-extensions-$currentVersion.module"))
		}
	}
}
