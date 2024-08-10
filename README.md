# Gradle Plugins

This repository contains Gradle plugins intended for use with Kord Extensions.

# Contents

- `docker/` - Docker Gradle plugin for Dockerfile generation.
- `kordex/` - Kord Extensions Gradle plugin for project configuration.
- `testModule/` - Separate Gradle module used to test the plugins.

# Docker Plugin

The Docker plugin helps to centralise your project's configuration by allowing you to generate
a Dockerfile via a Kotlin DSL.

For more information, [check out the documentation](https://docs.kordex.dev/docker-plugin.html).

# KordEx Plugin

The KordEx plugin exists to make it easier to set up and configure any project using Kord Extensions.
This is the canonical way to set up a KordEx project going forward.

For more information, [check out the documentation](https://docs.kordex.dev/kordex-plugin.html).
