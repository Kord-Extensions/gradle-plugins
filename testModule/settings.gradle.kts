pluginManagement {
	plugins {
		val pluginVersion = "1.7.4"

		kotlin("jvm") version "2.2.20"

		id("com.google.devtools.ksp") version "2.2.20-2.0.3"
		id("dev.kordex.gradle.docker") version pluginVersion
		id("dev.kordex.gradle.kordex") version pluginVersion
	}

    repositories {
	    mavenLocal()

        gradlePluginPortal()
	    mavenCentral()

	    maven("https://releases-repo.kordex.dev")
	    maven("https://snapshots-repo.kordex.dev")
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
