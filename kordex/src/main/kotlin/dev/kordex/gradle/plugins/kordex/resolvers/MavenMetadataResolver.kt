/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

@file:Suppress("StringLiteralDuplication")

package dev.kordex.gradle.plugins.kordex.resolvers

import dev.kordex.gradle.plugins.kordex.kordExReleasesUrlv1
import dev.kordex.gradle.plugins.kordex.kordExReleasesUrlv2
import dev.kordex.gradle.plugins.kordex.kordExSnapshotUrlv1
import dev.kordex.gradle.plugins.kordex.kordExSnapshotUrlv2
import dev.kordex.gradle.plugins.kordex.kordReleasesUrl
import dev.kordex.gradle.plugins.kordex.kordSnapshotUrl
import dev.kordex.gradle.plugins.kordex.resolvers.maven.MavenMetadata
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
import nl.adaptivity.xmlutil.serialization.XML
import org.slf4j.LoggerFactory

object MavenMetadataResolver {
	@Suppress("UnusedPrivateProperty")  // For now...
	private val logger = LoggerFactory.getLogger("MavenMetadataResolver")

	private val client = HttpClient {
		install(ContentNegotiation)
	}

	private val xml = XML {
		defaultPolicy {
			this.isStrictBoolean = false
		}
	}

	private val cache: MutableMap<String, MavenMetadata?> = mutableMapOf()
		@Synchronized get

	fun getMetadata(vararg urls: String): MavenMetadata? {
		return runBlocking {
			for (url in urls) {
				val result = cache.getOrPut(url) {
					val request = client.get(url)

					if (request.status == HttpStatusCode.NotFound) {
						null
					} else {
						val data = client.get(url).body<String>()

						MavenMetadata(xml.decodeFromString(data))
					}
				}

				if (result != null) {
					return@runBlocking result
				}
			}

			return@runBlocking null
		}
	}

	fun getKordExReleases() = getMetadata(
		kordExReleasesUrlv2("maven-metadata.xml"),
		kordExReleasesUrlv1("maven-metadata.xml"),
	)

	fun getKordExSnapshots() = getMetadata(
		kordExSnapshotUrlv2("maven-metadata.xml"),
		kordExSnapshotUrlv1("maven-metadata.xml")
	)

	fun getKordReleases() = getMetadata(kordReleasesUrl("maven-metadata.xml"))
	fun getKordSnapshots() = getMetadata(kordSnapshotUrl("maven-metadata.xml"))

	fun getKordExRelease(version: String) = if (version.startsWith("2.")) {
		getMetadata(kordExReleasesUrlv2("$version/maven-metadata.xml"))
	} else {
		getMetadata(kordExReleasesUrlv1("$version/maven-metadata.xml"))
	}

	fun getKordExSnapshot(version: String) = if (version.startsWith("2.")) {
		getMetadata(kordExSnapshotUrlv2("$version/maven-metadata.xml"))
	} else {
		getMetadata(kordExSnapshotUrlv1("$version/maven-metadata.xml"))
	}

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
