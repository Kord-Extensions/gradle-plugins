/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.kordex.gradle.plugins.kordex.plugins

import com.github.zafarkhaja.semver.expr.ExpressionParser
import dev.kordex.gradle.plugins.kordex.list
import dev.kordex.gradle.plugins.kordex.single
import org.gradle.api.internal.provider.PropertyFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property

class KordExPluginSettings(props: PropertyFactory) {
	val pluginClass: Property<String> = props.single()
	val id: Property<String> = props.single()
	val version: Property<String> = props.single()

	val author: Property<String> = props.single()
	val description: Property<String> = props.single()
	val license: Property<String> = props.single()

	val kordExVersionSpecifier: Property<String> = props.single()
	val dependencies: ListProperty<String> = props.list()

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
