import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    `kotlin-dsl`
    plugin
    val env = System.getenv()

    if (env.contains("GITHUB_ACTIONS") && !env.contains("NO_SIGNING")) {
        `signed-plugin`
    }

    id("com.gradle.plugin-publish") version "1.2.1"
}

val kordExKotlinVersion: String by properties

repositories {
    gradlePluginPortal()
    maven("https://repo.sleeping.town") {
        content {
            includeGroup("com.unascribed")
        }
    }
}

gradlePlugin {
    website = "https://kordex.dev"
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

val targetAttribute = Attribute.of("org.jetbrains.kotlin.platform.type", String::class.java)

dependencies {
    shadow(gradleApi())
    shadow(localGroovy())

	compileOnly(kotlin("gradle-plugin", version = "2.0.20-Beta1"))

	detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.23.6")
	detektPlugins("io.gitlab.arturbosch.detekt:detekt-rules-libraries:1.23.6")

    implementation(platform("io.ktor:ktor-bom:2.3.12"))
    implementation("io.ktor:ktor-client-cio")
    implementation("io.ktor:ktor-client-core")
    implementation("io.ktor:ktor-client-content-negotiation")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm")

	implementation("com.jcabi:jcabi-manifests:2.1.0")
	implementation("com.unascribed:flexver-java:1.0.2")
    implementation("io.github.pdvrieze.xmlutil:core-jvm:0.86.3")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json-jvm:1.6.3")

	implementation("io.github.pdvrieze.xmlutil:serialization-jvm:0.86.3"){
		exclude("io.github.pdvrieze.xmlutil", "core")
	}

}

tasks.withType<ShadowJar> {
    archiveClassifier = ""
}
