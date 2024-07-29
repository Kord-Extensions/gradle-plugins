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
