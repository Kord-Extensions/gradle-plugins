plugins {
	kotlin("jvm") version "2.0.20-Beta1"

	id("dev.kordex.gradle.kordex") version "1.1.1"
//	id("com.google.devtools.ksp") version "2.0.20-Beta1-1.0.22"
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
