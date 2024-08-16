plugins {
	kotlin("jvm")

	id("dev.kordex.gradle.kordex")
}

version = "1.0.0"

repositories {
	mavenCentral()
}

kordEx {
	bot {
		mainClass = "template.MainKt"
	}
}
