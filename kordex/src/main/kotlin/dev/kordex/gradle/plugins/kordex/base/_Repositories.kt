/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.kordex.gradle.plugins.kordex.base

import org.gradle.api.Project
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.kotlin.dsl.repositories

internal fun Project.repo(
	repoName: String,
	repoUrl: String,
	body:  MavenArtifactRepository.() -> Unit = {}
) {
	repositories {
		maven {
			name = repoName
			url = uri(repoUrl)

			body(this)
		}
	}
}
