pluginManagement {
	plugins {
		val pluginVersion = "1.5.0"

		kotlin("jvm") version "2.0.20"

		id("com.google.devtools.ksp") version "2.0.20-1.0.24"
		id("dev.kordex.gradle.docker") version pluginVersion
		id("dev.kordex.gradle.kordex") version pluginVersion
	}

    repositories {
        google()
        gradlePluginPortal()

	    maven("https://releases-repo.kordex.dev")
	    maven("https://snapshots-repo.kordex.dev")

	    mavenCentral()
        mavenLocal()
    }
}

buildscript {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()

        mavenLocal()
    }
}

rootProject.name = "testModule"

include(":submodule")
