plugins {
    java
    plugin

    `java-gradle-plugin`
    `kotlin-dsl`
    `maven-publish`
}

val ktorVersion = "2.3.12"
val kordExKotlinVersion: String by properties

gradlePlugin {
    website = "https://kordex.dev"
    vcsUrl = "https://github.com/Kord-Extensions/gradle-plugins"

    plugins {
        create("kordex") {
            description = "Gradle project plugin designed to make working with Kord Extensions simpler."

            displayName = "Kord Extensions"
            id = "dev.kordex.gradle.kordex"
            implementationClass = "dev.kordex.gradle.plugins.kordex.KordExPlugin"

            tags = setOf("kordex", "build", "kotlin", "api")
        }
    }
}

val targetAttribute = Attribute.of("org.jetbrains.kotlin.platform.type", String::class.java)

dependencies {
    implementation("com.jcabi:jcabi-manifests:2.1.0")

    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:$ktorVersion")

    implementation("io.github.pdvrieze.xmlutil:serialization-jvm:0.86.3"){
        exclude("io.github.pdvrieze.xmlutil", "core")
    }

    implementation("io.github.pdvrieze.xmlutil:core-jvm:0.86.3")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json-jvm:1.6.3")
}

tasks.withType<Jar> {
    manifest {
        attributes(
            mapOf("Kotlin-Version" to kordExKotlinVersion)
        )
    }
}

publishing {
    repositories {
        mavenLocal()
    }
}
