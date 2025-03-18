/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

@file:Suppress("UnstableApiUsage")

package dev.kordex.gradle.plugins.kordex

import org.gradle.api.problems.ProblemGroup
import org.gradle.api.problems.ProblemId

object ProblemGroups {
	val Base = ProblemGroup.create("kordex", "KordEx")
}

object ProblemIds {
	val WrongV1ModuleName = ProblemId.create(
		"wrong-v1-module-name",
		"Wrong v1 module name provided",
		ProblemGroups.Base
	)

	val WrongV2ModuleName = ProblemId.create(
		"wrong-v2-module-name",
		"Wrong v2 module name provided",
		ProblemGroups.Base
	)

	val IncompatibleKotlinVersion = ProblemId.create(
		"incompatible-kotlin-version",
		"Configured Kotlin plugin version is incompatible",
		ProblemGroups.Base
	)

	val JavaVersionTooOld = ProblemId.create(
		"java-version-too-old",
		"Configured Java version is too old",
		ProblemGroups.Base
	)

	val ProjectBothBotAndPlugin = ProblemId.create(
		"both-bot-and-plugin",
		"Project is both bot and plugin",
		ProblemGroups.Base
	)
}
