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
	id("dev.kordex.gradle.kordex") version "1.0.1"
}

kordEx {
	// ...
}
```

## Functionality

This plugin provides a set of default configurations for building bots with Kord Extensions, allowing for a cleaner
`build.gradle.kts` and removing the need to manually configure dependencies and repositories when they need to be
changed.

Here's everything this plugin can do for you:

- Add a generated `kordex.properties` file to your bot's `.jar` file, containing:
  - Your configured data collection settings.
  - The version of Kord and KordEx you're using.
  - A list of first-party KordEx modules you're using.
- Add the Maven repositories you'll need to work with Kord and KordEx:
  - KordEx, Google, Maven Central, OSSRH (`oss.sonatype.org` and `s01.oss.sonatype.org`).
  - When using the `extra-mappings` module: FabricMC, QuiltMC, Shedaniel, JitPack.
- Add the dependencies you'll need to work with Kord and KordEx:
  - The specified version of KordEx, or the latest version if not specified.
  - The specified version of Kord (with or without the voice module), or the latest version if not specified.
  - The dependencies that any first-party modules you've configured require:
    - The module dependency itself.
    - When using the `adapter-mongodb` module: The latest versions of `mongodb-driver-kotlin-coroutine` and
      `bson-kotlinx`.
- Check whether you're using the same Kotlin version that KordEx uses.
- Configure the `application` plugin and `jar` task, when provided with the `mainClass` setting.
- Configure the Kotlin plugin with the required compiler arguments, and the same JVM target used by KordEx.
- Configure the Java plugin with the same Java source/target compatibility settings used by KordEx.
- Configure KordEx's future data collection settings, defaulting to `STANDARD` level data collection.
  We'll provide more information on what this means closer to release time.

## Options

The plugin supports the following options, exposed via the `kordEx` builder:

- `addRepositiories = true` - Whether to add the Maven repositories that you'll need to grab KordEx and its 
  dependencies at build time.
- `ignoreIncompatibleKotlinVersion = false` - Whether to warn about incompatible Kotlin versions instead of error.
- `kordVersion = null` - A specific Kord version to pin, instead of the one KordEx was built against.
- `kordExVersion = null` - A specific KordEx version to pin, instead of the latest version.
- `mainClass = null` - Your bot's main class, used to configure the `application` plugin and `jar` task.
- `voice = true` - Whether to use a version of Kord that supports voice, which will result in a larger dependency.

It also exposes the following functions:

- `dataCollection(level)` - Set your bot's data collection settings. 
  **Note:** We haven't implemented data collection yet, but you can configure it ahead of time with this function.
- `module(name)` - Add a dependency on a KordEx module by name.

All settings are optional, and you can omit the `kordEx` builder if you just want the default configuration.

## Migrating

When migrating your project to this plugin, make sure you follow these instructions:

1. Add the plugin as described above.
2. Configure it as needed, adding the KordEx modules you need by artefact ID (eg `unsafe`, `extra-pluralkit`, etc.).
3. Remove the following dependencies from your Gradle project, as the plugin adds them automatically:
    - Any dependencies on Kord or KordEx.
    - Any module-specific dependencies:
        - For `adapter-mongodb` - `mongodb-driver-kotlin-coroutine` and `bson-kotlinx`
4. Remove the following repositories from your Gradle project, as the plugin adds them automatically:
    - `google()` and `mavenCentral()`
    - OSSRH - `oss.sonatype.org`, `s01.oss.sonatype.org`
    - Any module-specific repositories:
        - For `extra-mappings`:
            - FabricMC - `maven.fabricmc.net`
            - QuiltMC - `maven.quiltmc.org` (both releases and snapshots)
            - Shedaniel - `maven.shedaniel.me`
            - JitPack - `jitpack.io`
5. Remove the `application` plugin and `Main-Class` JAR manifest, and configure your main class via the `mainClass`
   property in the KordEx builder.
6. Remove the `sourceCompatibility` and `targetCompatibility` options from your `java` builder.
7. Remove the `jvmTarget` option from your Kotlin task configuration.
   Similarly, if the only compiler argument you're providing is an opt-in on `kotlin.RequiresOptIn`, remove it.
8. Ensure your project builds correctly before pushing your changes.
