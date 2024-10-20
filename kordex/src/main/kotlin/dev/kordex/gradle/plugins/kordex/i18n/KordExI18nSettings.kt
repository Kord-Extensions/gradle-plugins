/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.kordex.gradle.plugins.kordex.i18n

import dev.kordex.gradle.plugins.kordex.boolean
import dev.kordex.gradle.plugins.kordex.single
import org.gradle.api.internal.provider.PropertyFactory
import org.gradle.api.provider.Property
import java.io.File

class KordExI18nSettings(props: PropertyFactory) {
	val classPackage: Property<String> = props.single()
	val translationBundle: Property<String> = props.single()

	val className: Property<String> = props.single("Translations")
	val configureSourceSet: Property<Boolean> = props.boolean(true)
	val outputDirectory: Property<File> = props.single()
}
