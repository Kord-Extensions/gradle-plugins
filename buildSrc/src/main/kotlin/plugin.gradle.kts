plugins {
	id("dev.yumi.gradle.licenser")

	java
	signing

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

signing {
	val signingKey: String? by project ?: return@signing
	val signingPassword: String? by project ?: return@signing

	useInMemoryPgpKeys(signingKey, signingPassword)

//	sign(publishing.publications["maven"])
}
