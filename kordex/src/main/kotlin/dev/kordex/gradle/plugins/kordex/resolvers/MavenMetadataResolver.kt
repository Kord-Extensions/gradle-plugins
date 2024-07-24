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
import dev.kordex.gradle.plugins.kordex.resolvers.maven.MavenMetadata
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
import nl.adaptivity.xmlutil.serialization.XML
import org.slf4j.LoggerFactory

class MavenMetadataResolver {
	private val logger = LoggerFactory.getLogger("MavenMetadataResolver")

	private val client = HttpClient {
		install(ContentNegotiation)
	}

	private val xml = XML {
		defaultPolicy {
			this.isStrictBoolean = false
		}
	}

	private val cache: MutableMap<String, MavenMetadata> = mutableMapOf()
		@Synchronized get


	fun getMetadata(url: String): MavenMetadata = runBlocking {
		cache.getOrPut(url) {
			val data = client.get(url).body<String>()

			MavenMetadata(xml.decodeFromString(data))
		}
	}

	fun getKordExReleases() = getMetadata(kordExReleasesUrl("maven-metadata.xml"))
	fun getKordExSnapshots() = getMetadata(kordExSnapshotUrl("maven-metadata.xml"))
	fun getKordReleases() = getMetadata(kordReleasesUrl("maven-metadata.xml"))
	fun getKordSnapshots() = getMetadata(kordSnapshotUrl("maven-metadata.xml"))

	fun getKordExRelease(version: String) = getMetadata(kordExReleasesUrl("$version/maven-metadata.xml"))
	fun getKordExSnapshot(version: String) = getMetadata(kordExSnapshotUrl("$version/maven-metadata.xml"))
	fun getKordRelease(version: String) = getMetadata(kordReleasesUrl("$version/maven-metadata.xml"))
	fun getKordSnapshot(version: String) = getMetadata(kordSnapshotUrl("$version/maven-metadata.xml"))

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
}
