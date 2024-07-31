plugins {
	kotlin("jvm") version "2.0.20-Beta2"

	id("dev.kordex.gradle.kordex") version "1.1.0"
}

version = "1.0.0"

repositories {
	mavenCentral()
}

kordEx {
	ignoreIncompatibleKotlinVersion = true

	bot {
		mainClass = "template.MainKt"
	}
}
