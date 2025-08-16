plugins {
	`kotlin-dsl`
	plugin
	`tested-plugin`

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
	website = "https://docs.kordex.dev/docker-plugin.html"
	vcsUrl = "https://github.com/Kord-Extensions/gradle-plugins"

	plugins {
		create("docker") {
			description = "Gradle plugin allowing automatic generation of Dockerfiles."
			displayName = "Docker Generator"
			tags = setOf("kordEx", "build", "kotlin", "docker", "container")

			id = "dev.kordex.gradle.docker"
			implementationClass = "dev.kordex.gradle.plugins.docker.DockerPlugin"
		}
	}
}

dependencies {
	compileOnly(kotlin("gradle-plugin", libs.versions.kotlin.gradle.plugin.get()))

	detektPlugins(libs.bundles.detekt.plugins)

	implementation(libs.kx.ser)
}

