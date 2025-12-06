plugins {
	`kotlin-dsl`
}

repositories {
	google()
	gradlePluginPortal()

	mavenCentral()
}

dependencies {
	implementation(kotlin("gradle-plugin", version = "2.2.0"))
	implementation(kotlin("serialization", version = "2.2.0"))
	implementation(kotlin("power-assert", version = "2.2.0"))

	implementation("dev.yumi", "yumi-gradle-licenser", "1.2.0")
	implementation("io.gitlab.arturbosch.detekt", "detekt-gradle-plugin", "1.23.8")
	implementation("org.sonarqube:org.sonarqube.gradle.plugin:7.1.0.6387")
	implementation("org.jetbrains.kotlinx.kover:org.jetbrains.kotlinx.kover.gradle.plugin:0.9.3")

	implementation(gradleApi())
	implementation(localGroovy())
}

beforeEvaluate {
	val projectVersion: String by project

	group = "dev.kordex.gradle"
	version = projectVersion
}
