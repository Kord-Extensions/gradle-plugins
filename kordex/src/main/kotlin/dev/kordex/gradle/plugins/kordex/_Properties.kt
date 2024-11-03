/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.kordex.gradle.plugins.kordex

import org.gradle.api.internal.provider.PropertyFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property

@InternalAPI
inline fun <reified T> PropertyFactory.single(): Property<T> =
	property(T::class.java)

@InternalAPI
inline fun <reified T> PropertyFactory.list(): ListProperty<T> =
	listProperty(T::class.java)

@InternalAPI
fun PropertyFactory.boolean(): Property<Boolean> =
	property(Boolean::class.javaObjectType)

@InternalAPI
fun PropertyFactory.boolean(default: Boolean): Property<Boolean> =
	property(Boolean::class.javaObjectType).convention(default)

@InternalAPI
fun PropertyFactory.booleanList(): ListProperty<Boolean> =
	listProperty(Boolean::class.javaObjectType)

@InternalAPI
inline fun <reified T> PropertyFactory.single(default: T): Property<T> =
	property(T::class.java).convention(default)
