/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.kordex.gradle.plugins.kordex

const val CENTRAL_BASE = "https://repo1.maven.org/maven2"
const val OSS_BASE = "https://oss.sonatype.org/content/repositories/snapshots"
const val S01_BASE = "https://s01.oss.sonatype.org/content/repositories/snapshots"

const val KORDEX_RELEASES_BASE = "$CENTRAL_BASE/com/kotlindiscord/kord/extensions/kord-extensions"
const val KORDEX_SNAPSHOTS_BASE = "$S01_BASE/com/kotlindiscord/kord/extensions/kord-extensions"

const val KORD_RELEASES_BASE = "$CENTRAL_BASE/dev/kord/kord-core"
const val KORD_SNAPSHOTS_BASE = "$OSS_BASE/dev/kord/kord-core"

fun kordExReleasesUrl(path: String) =
	"$KORDEX_RELEASES_BASE/$path"

fun kordExSnapshotUrl(path: String) =
	"$KORDEX_SNAPSHOTS_BASE/$path"

fun kordReleasesUrl(path: String) =
	"$KORD_RELEASES_BASE/$path"

fun kordSnapshotUrl(path: String) =
	"$KORD_SNAPSHOTS_BASE/$path"
