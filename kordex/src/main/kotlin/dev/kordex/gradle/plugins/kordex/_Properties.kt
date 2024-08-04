/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.kordex.gradle.plugins.kordex

import org.gradle.api.internal.provider.DefaultListProperty
import org.gradle.api.internal.provider.DefaultProperty
import org.gradle.api.internal.provider.PropertyFactory

inline fun <reified T> PropertyFactory.single(): DefaultProperty<T> =
	property(T::class.java)

inline fun <reified T> PropertyFactory.list(): DefaultListProperty<T> =
	listProperty(T::class.java)

fun PropertyFactory.boolean(): DefaultProperty<Boolean> =
	property(Boolean::class.javaObjectType)

fun PropertyFactory.booleanList(): DefaultListProperty<Boolean> =
	listProperty(Boolean::class.javaObjectType)
