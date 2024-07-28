plugins {
	id("com.github.johnrengelman.shadow")
	id("dev.yumi.gradle.licenser")

	id("io.gitlab.arturbosch.detekt")

	kotlin("plugin.serialization")
}

repositories {
	google()
	gradlePluginPortal()
	mavenCentral()
}

dependencies {
	shadow(gradleApi())
	shadow(localGroovy())
}

detekt {
	buildUponDefaultConfig = true
	config.from(rootProject.file("detekt.yml"))

	autoCorrect = true
}

license {
	rule(rootProject.file("codeformat/HEADER"))
}
