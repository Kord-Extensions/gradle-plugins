/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.kordex.gradle.plugins.kordex.extensions

open class KordExExtension {
	var addRepositories: Boolean = true
	var ignoreIncompatibleKotlinVersion: Boolean = false

	var voice: Boolean = true

	var kordVersion: String? = null
	var kordExVersion: String? = null
}
