import org.gradle.kotlin.dsl.kotlin

pluginManagement {
	plugins {
		val pluginVersion = "1.9.2"

		kotlin("jvm") version "2.2.20"

		id("com.google.devtools.ksp") version "2.3.3"
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
	    mavenLocal()

        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

rootProject.name = "testModule"

include(":submodule")
