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

	maven("https://releases-repo.kordex.dev")
	maven("https://snapshots-repo.kordex.dev")
}

gradlePlugin {
	website = "https://docs.kordex.dev/bots/gradle-plugin"
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
	compileOnly(kotlin("gradle-plugin", libs.versions.kotlin.gradle.plugin.get()))

	detektPlugins(libs.bundles.detekt.plugins)

	implementation(libs.bundles.ktor.client)
	implementation(libs.flexver)
	implementation(libs.java.semver)
	implementation(libs.jcabi.manifests)
	implementation(libs.kotlinpoet)
	implementation(libs.kotlinpoet.dsl)
	implementation(libs.kx.ser)
	implementation(libs.xmlutil.core)

	implementation(libs.xmlutil.ser){
		exclude("io.github.pdvrieze.xmlutil", "core")
	}
}
