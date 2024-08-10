import dev.kordex.gradle.plugins.docker.file.*
import dev.kordex.gradle.plugins.docker.file.commands.RunCommand

plugins {
	kotlin("jvm") version "2.0.20-Beta1"

	id("dev.kordex.gradle.docker") version "1.2.0"
	id("dev.kordex.gradle.kordex") version "1.2.0"
//	id("com.google.devtools.ksp") version "2.0.20-Beta1-1.0.22"
}

version = "1.0.0"

repositories {
	mavenCentral()
}

kordEx {
//	jvmTarget = 17

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
		comment("Added to test the output - this Dockerfile isn't meant to work!")

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
