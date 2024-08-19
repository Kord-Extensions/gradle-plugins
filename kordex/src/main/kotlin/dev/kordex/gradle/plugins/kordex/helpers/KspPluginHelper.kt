/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.kordex.gradle.plugins.kordex.helpers

import org.gradle.api.Project
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.kotlin.dsl.*
import org.slf4j.LoggerFactory

object KspPluginHelper {
	private val logger = LoggerFactory.getLogger(KspPluginHelper::class.java)

	fun apply(target: Project) {
		target.pluginManager.withPlugin("com.google.devtools.ksp") {
			logger.info("KSP plugin detected, adding Kord Extensions annotation processor")

			val sourceSets = target.extensions.getByType<SourceSetContainer>()

			sourceSets.getByName("main") {
				java {
					srcDir(target.layout.buildDirectory.file("generated/ksp/main/kotlin/"))
				}
			}

			sourceSets.getByName("test") {
				java {
					srcDir(target.layout.buildDirectory.file("generated/ksp/test/kotlin/"))
				}
			}
		}
	}
}
