# Gradle Plugins

This repository contains Gradle plugins intended for use with Kord Extensions.

# Contents

- `kordex/` - Kord Extensions Gradle plugin for project configuration.
- `testModule/` - Separate Gradle module used to test the plugins.

# KordEx Plugin

The KordEx plugin exists to make it easier to set up and configure any project using Kord Extensions.
This is the canonical way to set up a KordEx project going forward.

```kt
plugins {
	id("dev.kordex.gradle.kordex") version "1.0.0"
}

kordEx {
	// ...
}
```

## Options

The plugin supports the following options, exposed via the `kordEx` builder:

- `addRepositiories = true` - Whether to add the repos you'll need to grab KordEx and its dependencies at build time.
- `ignoreIncompatibleKotlinVersion = false` - Whether to warn about incompatible Kotlin versions instead of error.
- `kordVersion = null` - A specific Kord version to pin, instead of the one KordEx was built against.
- `kordExVersion = null` - A specific KordEx version to pin, instead of the latest version.
- `voice = true` - Whether to use a version of Kord that supports voice, which will result in a larger dependency.

It also exposes the following functions:

- `dataCollection(level)` - Set your bot's data collection settings. **Note:** We haven't implemented data collection yet.
- `module(name)` - Add a dependency on a KordEx module by name.

## Migrating

When migrating your project to this plugin, make sure you follow these instructions:

1. Add the plugin as described above.
2. Configure it as needed, adding the KordEx modules you need by artefact ID (eg `unsafe`, `extra-pluralkit`, etc.).
3. Remove the following dependencies from your Gradle project, as the plugin adds them automatically:
    - Any dependencies on Kord or KordEx.
    - Any module-specific dependencies:
        - For `adapter-mongodb` - `mongodb-driver-kotlin-coroutine` and `bson-kotlinx`
4. Remove the following repositories from your Gradle project, as the plugin adds them automatically:
    - OSSRH - `oss.sonatype.org`, `s01.oss.sonatype.org`
    - Any module-specific repositories:
        - For `extra-mappings`:
            - FabricMC - `maven.fabricmc.net`
            - QuiltMC - `maven.quiltmc.org` (both releases and snapshots)
            - Shedaniel - `maven.shedaniel.me`
            - JitPack - `jitpack.io`
5. Ensure your project builds correctly before pushing your changes.
