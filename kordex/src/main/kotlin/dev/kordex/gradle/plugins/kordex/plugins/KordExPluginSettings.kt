/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.kordex.gradle.plugins.kordex.plugins

import com.github.zafarkhaja.semver.expr.ExpressionParser
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property

interface KordExPluginSettings {
	val id: Property<String>
	val version: Property<String>

	val author: Property<String>
	val descriptor: Property<String>
	val license: Property<String>

	val kordExVersionSpecifier: Property<String>
	val dependencies: ListProperty<String>

	fun dependency(id: String, versionSpecifier: String? = null, optional: Boolean = false) {
		// Try to parse the expression as pf4j does, to check validity.
		ExpressionParser.newInstance().parse(versionSpecifier)

		dependencies.add(
			buildString {
				append(id)

				if (optional) {
					append("?")
				}

				if (versionSpecifier != null) {
					append("@")
					append(versionSpecifier)
				}
			}
		)
	}

	fun kordExVersion(spec: String) {
		// Try to parse the expression as pf4j does, to check validity.
		ExpressionParser.newInstance().parse(spec)

		kordExVersionSpecifier.set(spec)
	}
}
