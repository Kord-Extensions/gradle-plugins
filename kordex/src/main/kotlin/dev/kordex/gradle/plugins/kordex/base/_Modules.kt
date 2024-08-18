/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.kordex.gradle.plugins.kordex.base

import dev.kordex.gradle.plugins.kordex.MAPPINGS_V1
import dev.kordex.gradle.plugins.kordex.MAPPINGS_V2
import dev.kordex.gradle.plugins.kordex.MONGODB_V1
import dev.kordex.gradle.plugins.kordex.MONGODB_V2
import dev.kordex.gradle.plugins.kordex.Version
import dev.kordex.gradle.plugins.kordex.isKX2
import org.slf4j.LoggerFactory

val v1to2 = mapOf(
	MAPPINGS_V1 to MAPPINGS_V2,
	MONGODB_V1 to MONGODB_V2,

	"java-time" to "dev-java-time",
	"time4j" to "dev-time4j",
	"unsafe" to "dev-unsafe",

	"extra-phishing" to "func-phishing",
	"extra-pluralkit" to "pluralkit",
	"extra-tags" to "func-tags",
	"extra-welcome" to "func-welcome",
)

val v2tov1 = v1to2.entries.map { (k, v) -> v to k }.toMap()

val logger = LoggerFactory.getLogger("kordex.modules")

fun List<String>.normalizeModules(kordExVersion: Version, log: Boolean = true): List<String> =
	if (kordExVersion.isKX2) {
		map {
			if (it in v1to2) {
				if (log) {
					logger.warn(
						"WARNING | Module '$it' was specified, but the v2 version is called '${v1to2[it]}'. " +
							"This will become an error in later versions of the KordEx plugin."
					)
				}

				v1to2[it]!!
			} else {
				it
			}
		}
	} else {
		map {
			if (it in v2tov1) {
				if (log) {
					logger.warn(
						"WARNING | Module '$it' was specified, but the v1 version is called '${v2tov1[it]}'. " +
							"This will become an error in later versions of the KordEx plugin."
					)
				}

				v2tov1[it]!!
			} else {
				it
			}
		}
	}
