/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.kordex.gradle.plugins.kordex

const val CENTRAL_BASE = "https://repo1.maven.org/maven2"
const val OSS_BASE = "https://oss.sonatype.org/content/repositories/snapshots"
const val S01_BASE = "https://s01.oss.sonatype.org/content/repositories/snapshots"

const val KORDEX_RELEASES = "https://releases-repo.kordex.dev"
const val KORDEX_SNAPSHOTS = "https://snapshots-repo.kordex.dev"

const val KORDEX_RELEASES_BASE_V1 = "$KORDEX_RELEASES/com/kotlindiscord/kord/extensions/kord-extensions"
const val KORDEX_SNAPSHOTS_BASE_V1 = "$KORDEX_SNAPSHOTS/com/kotlindiscord/kord/extensions/kord-extensions"

const val KORDEX_RELEASES_BASE_V2 = "$KORDEX_RELEASES/dev/kordex/kord-extensions"
const val KORDEX_SNAPSHOTS_BASE_V2 = "$KORDEX_SNAPSHOTS/dev/kordex/kord-extensions"

const val KORD_RELEASES_BASE = "$CENTRAL_BASE/dev/kord/kord-core"
const val KORD_SNAPSHOTS_BASE = "$OSS_BASE/dev/kord/kord-core"

fun kordExReleasesUrlv1(path: String) =
	"$KORDEX_RELEASES_BASE_V1/$path"

fun kordExSnapshotUrlv1(path: String) =
	"$KORDEX_SNAPSHOTS_BASE_V1/$path"

fun kordExReleasesUrlv2(path: String) =
	"$KORDEX_RELEASES_BASE_V2/$path"

fun kordExSnapshotUrlv2(path: String) =
	"$KORDEX_SNAPSHOTS_BASE_V2/$path"

fun kordReleasesUrl(path: String) =
	"$KORD_RELEASES_BASE/$path"

fun kordSnapshotUrl(path: String) =
	"$KORD_SNAPSHOTS_BASE/$path"
