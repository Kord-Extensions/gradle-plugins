plugins {
	id("dev.yumi.gradle.licenser")

	kotlin("plugin.serialization")
	com.github.johnrengelman.shadow
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

license {
	rule(rootProject.file("codeformat/HEADER"))
}

