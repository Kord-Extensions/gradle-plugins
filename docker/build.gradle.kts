plugins {
	`kotlin-dsl`
	plugin
	val env = System.getenv()

	if (env.contains("GITHUB_ACTIONS") && !env.contains("NO_SIGNING")) {
		`signed-plugin`
	}

	id("com.gradle.plugin-publish") version "1.2.2"
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
	compileOnly(kotlin("gradle-plugin", version = "2.0.20"))

	detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.23.6")
	detektPlugins("io.gitlab.arturbosch.detekt:detekt-rules-libraries:1.23.6")

	implementation("org.jetbrains.kotlinx:kotlinx-serialization-json-jvm:1.6.3")
}

