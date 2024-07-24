plugins {
	kotlin("jvm") version "2.0.20-Beta1"

	id("dev.kordex.gradle.kordex") version "1.0.1"
}

version = "1.0.0"

repositories {
	mavenCentral()
}

kordEx {
	mainClass = "template.MainKt"
}
