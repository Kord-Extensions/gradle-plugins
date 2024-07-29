plugins {
	id("dev.yumi.gradle.licenser")

	id("io.gitlab.arturbosch.detekt")

	kotlin("jvm")
	kotlin("plugin.serialization")
}

repositories {
	google()
	gradlePluginPortal()
	mavenCentral()
}

dependencies {
	compileOnly(gradleApi())
	compileOnly(localGroovy())
}

detekt {
	buildUponDefaultConfig = true
	config.from(rootProject.file("detekt.yml"))

	autoCorrect = true
}

license {
	rule(rootProject.file("codeformat/HEADER"))
}
