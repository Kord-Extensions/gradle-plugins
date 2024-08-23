import dev.kordex.gradle.plugins.docker.file.*
import dev.kordex.gradle.plugins.docker.file.commands.*
import org.gradle.kotlin.dsl.docker

plugins {
	kotlin("jvm")

	id("com.google.devtools.ksp")
	id("dev.kordex.gradle.docker")
	id("dev.kordex.gradle.kordex")
}

version = "1.0.0"

repositories {
	mavenCentral()
}

kordEx {
	module("data-mongodb")
	module("extra-pluralkit")
	module("func-mappings")

	kordVersion = "latest"

	bot {
		mainClass = "template.MainKt"
	}
}

docker {
	file(rootProject.file("Dockerfile"))

	commands {
		from("openjdk:21-jdk-slim")

		emptyLine()

		runShell("mkdir -p /bot/plugins")
		runShell("mkdir -p /bot/data")

		emptyLine()

		copy("build/libs/$name-*-all.jar", "/bot/bot.jar")
		volume("/bot/data", "/bot/plugins")

		emptyLine()

		workdir("/bot")

		emptyLine()

		entryPointExec(
			"java", "-Xms2G", "-Xmx2G",
			"-jar", "/bot/bot.jar"
		)

		emptyLine()

		comment("Added to test the output - this Dockerfile isn't meant to work in prod!")

		runExec("banana") {
			bindMount {
				from = "fromProp"
				target = "targetProp"

				readWrite = true
			}

			networkType(RunCommand.NetworkType.Host)
			securityType(RunCommand.SecurityType.Sandbox)
		}
	}
}
