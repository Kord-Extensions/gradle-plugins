/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

@file:Suppress("UnstableApiUsage")

package dev.kordex.gradle.plugins.kordex.base

import dev.kordex.gradle.plugins.kordex.MAPPINGS_V1
import dev.kordex.gradle.plugins.kordex.MAPPINGS_V2
import dev.kordex.gradle.plugins.kordex.MONGODB_V1
import dev.kordex.gradle.plugins.kordex.MONGODB_V2
import dev.kordex.gradle.plugins.kordex.ProblemIds
import dev.kordex.gradle.plugins.kordex.Version
import dev.kordex.gradle.plugins.kordex.isKX2
import org.gradle.api.problems.Problem
import org.gradle.api.problems.ProblemReporter
import org.gradle.api.problems.Severity
import org.slf4j.Logger
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

val v2tov1 = v1to2.entries.associate { (k, v) -> v to k }

val logger: Logger = LoggerFactory.getLogger("kordex.modules")

fun List<String>.normalizeModules(kordExVersion: Version, reporter: ProblemReporter, log: Boolean = true): List<String> {
	val problems = mutableListOf<Problem>()

	val result = if (kordExVersion.isKX2) {
		map {
			if (it in v1to2) {
				problems.add(
					reporter.create(ProblemIds.WrongV2ModuleName) {
						details("v1 module '$it' was specified, but the v2 version is '${v1to2[it]}'")
						solution("Specify '${v1to2[it]}' instead of '$it'")
						severity(Severity.ERROR)
					}
				)

				v1to2[it]!!
			} else {
				it
			}
		}
	} else {
		map {
			if (it in v2tov1) {
				problems.add(
					reporter.create(ProblemIds.WrongV1ModuleName) {
						details("v2 module '$it' was specified, but the v1 version is '${v2tov1[it]}'")
						solution("Specify '${v2tov1[it]}' instead of '$it'")
						severity(Severity.ERROR)
					}
				)

				v2tov1[it]!!
			} else {
				it
			}
		}
	}

	if (log && problems.isNotEmpty()) {
		reporter.throwing(
			RuntimeException("Incorrect module names detected"),
			problems
		)
	}

	return result
}
