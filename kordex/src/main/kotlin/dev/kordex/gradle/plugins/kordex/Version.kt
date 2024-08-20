/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.kordex.gradle.plugins.kordex

import dev.kordex.libs.com.unascribed.flexver.flexver.FlexVerComparator

data class Version(val version: String) {
	override fun toString(): String =
		version
}

operator fun Version?.compareTo(other: Version?): Int {
	if ((this == null) xor (other == null)) {
		if (this == null) {
			return -1
		}

		return 1
	} else if (this == null && other == null) {
		return 0
	}

	return FlexVerComparator.compare(this!!.version, other!!.version)
}

operator fun String?.compareTo(other: Version?): Int {
	if ((this == null) xor (other == null)) {
		if (this == null) {
			return -1
		}

		return 1
	} else if (this == null && other == null) {
		return 0
	}

	return FlexVerComparator.compare(this!!, other!!.version)
}

operator fun Version?.compareTo(other: String?): Int {
	if ((this == null) xor (other == null)) {
		if (this == null) {
			return -1
		}

		return 1
	} else if (this == null && other == null) {
		return 0
	}

	return FlexVerComparator.compare(this!!.version, other!!)
}

// "2.1.0-20240820.163613-3"
private val SNAPSHOT_REGEX = "\\d+\\.\\d+\\.\\d+-\\d+\\.\\d+-\\d+".toRegex()

fun Version.normalize(): Version {
	if (version.matches(SNAPSHOT_REGEX)) {
		return Version(version.split("-", limit = 2).first() + "-SNAPSHOT")
	}

	return this
}

val Version.isKX2: Boolean
	get() = version.startsWith("2.")
