pluginManagement {
	plugins {
		val pluginVersion = "1.3.2"

		kotlin("jvm") version "2.0.20-Beta1"

		id("dev.kordex.gradle.docker") version pluginVersion
		id("dev.kordex.gradle.kordex") version pluginVersion
	}

    repositories {
        google()
        gradlePluginPortal()
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
