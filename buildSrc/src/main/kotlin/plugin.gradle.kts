plugins {
	id("dev.yumi.gradle.licenser")

	java

	kotlin("jvm")
	kotlin("plugin.serialization")
}

repositories {
	google()
	gradlePluginPortal()
	mavenCentral()
}

dependencies {
	implementation(gradleApi())
	implementation(localGroovy())
}

license {
	rule(rootProject.file("codeformat/HEADER"))
}

