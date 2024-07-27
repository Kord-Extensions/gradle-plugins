plugins {
	`kotlin-dsl`
}

repositories {
	google()
	gradlePluginPortal()

	mavenCentral()
}

dependencies {
	implementation(kotlin("gradle-plugin", version = "1.9.23"))
	implementation(kotlin("serialization", version = "1.9.23"))

	implementation("dev.yumi", "yumi-gradle-licenser", "1.2.0")
	implementation("com.github.johnrengelman", "shadow", "8.1.1")

	implementation(gradleApi())
	implementation(localGroovy())
}

beforeEvaluate {
	val projectVersion: String by project

	group = "dev.kordex.gradle"
	version = projectVersion
}
