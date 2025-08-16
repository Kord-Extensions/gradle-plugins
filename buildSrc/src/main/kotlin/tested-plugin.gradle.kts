import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

val current = project

plugins {
	java
	kotlin("plugin.power-assert")

	id("org.jetbrains.kotlinx.kover")
}

tasks {
	test {
		useJUnitPlatform()

		testLogging.showStandardStreams = true

		testLogging {
			events("PASSED", "FAILED", "SKIPPED", "STANDARD_OUT", "STANDARD_ERROR")
		}

		systemProperty("org.slf4j.simpleLogger.defaultLogLevel", "debug")
	}
}

@OptIn(ExperimentalKotlinGradlePluginApi::class)
powerAssert {
	functions = listOf(
		"kotlin.assert",

		"kotlin.test.assertEquals",
		"kotlin.test.assertNull",
		"kotlin.test.assertTrue",

		"kotlin.require",
	)
}

dependencies {
	testImplementation(kotlin("test"))
}

rootProject.dependencies {
	kover(current)
}
