pluginManagement {
	plugins {
		val pluginVersion = "1.4.0"

		kotlin("jvm") version "2.0.20-Beta1"

		id("com.google.devtools.ksp") version "2.0.20-Beta1-1.0.22"
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
