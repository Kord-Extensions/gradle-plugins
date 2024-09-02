plugins {
	`kotlin-dsl`
	plugin
	val env = System.getenv()

	if (env.contains("GITHUB_ACTIONS") && !env.contains("NO_SIGNING")) {
		`signed-plugin`
	}

	id("com.gradle.plugin-publish") version "1.2.1"
}

repositories {
	gradlePluginPortal()
}

gradlePlugin {
	website = "https://docs.kordex.dev/kordex-plugin.html"
	vcsUrl = "https://github.com/Kord-Extensions/gradle-plugins"

	plugins {
		create("kordex") {
			description = "Gradle project plugin designed to make working with Kord Extensions simpler."
			displayName = "Kord Extensions"
			tags = setOf("kordEx", "build", "kotlin", "api", "kord", "discord")

			id = "dev.kordex.gradle.kordex"
			implementationClass = "dev.kordex.gradle.plugins.kordex.KordExPlugin"
		}
	}
}

dependencies {
	compileOnly(kotlin("gradle-plugin", version = "2.0.20"))

	detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.23.6")
	detektPlugins("io.gitlab.arturbosch.detekt:detekt-rules-libraries:1.23.6")

	implementation(platform("io.ktor:ktor-bom:2.3.12"))
	implementation("io.ktor:ktor-client-cio")
	implementation("io.ktor:ktor-client-core")
	implementation("io.ktor:ktor-client-content-negotiation")
	implementation("io.ktor:ktor-serialization-kotlinx-json-jvm")

	implementation("com.jcabi:jcabi-manifests:2.1.0")
	implementation("io.github.pdvrieze.xmlutil:core-jvm:0.86.3")
	implementation("com.github.zafarkhaja:java-semver:0.10.2")
	implementation("org.jetbrains.kotlinx:kotlinx-serialization-json-jvm:1.7.2")

	implementation("io.github.pdvrieze.xmlutil:serialization-jvm:0.86.3"){
		exclude("io.github.pdvrieze.xmlutil", "core")
	}
}

license {
	exclude("dev/kordex/libs/com/unascribed/flexver/flexver/FlexVerComparator.java")
}
