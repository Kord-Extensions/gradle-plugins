/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.kordex.gradle.plugins.kordex.helpers

import dev.kordex.gradle.plugins.kordex.InternalAPI
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension
import org.slf4j.LoggerFactory

@InternalAPI
object KspPluginHelper {
	private val logger = LoggerFactory.getLogger(KspPluginHelper::class.java)

	fun apply(target: Project) {
		target.pluginManager.withPlugin("com.google.devtools.ksp") {
			logger.info("KSP | Configuring source sets...")

 			target.afterEvaluate {
			    val sourceSets = target.kotlinExtension.sourceSets

				sourceSets.named("main").configure {
					kotlin {
						srcDir(target.tasks.named("kspKotlin"))
// 					    srcDir(target.layout.buildDirectory.file("generated/ksp/main/kotlin/"))
					}
				}

				sourceSets.named("test").configure {
					kotlin {
						srcDir(target.tasks.named("kspTestKotlin"))
// 					    srcDir(target.layout.buildDirectory.file("generated/ksp/test/kotlin/"))
					}
				}

			    logger.info("KSP | Adding `jar` task soft dependency on `kspTestKotlin` to avoid breakages...")

			    target.tasks.named("jar").configure {
					mustRunAfter(tasks.named("kspTestKotlin"))
			    }
 			}
		}
	}
}
