plugins {
	id("org.jetbrains.kotlinx.kover")
	id("org.sonarqube")
}

val projectVersion: String by project

group = "dev.kordex.gradle.plugins"
version = projectVersion

sonar {
	val org = "Kord-Extensions"
	val gitUrl = "https://github.com/${org}/gradle-plugins/"
	val homepageUrl = "https://kordex.dev"

	properties {
		property("sonar.sourceEncoding", "UTF-8")
		property("sonar.projectName", "gradle-plugins")
		property("sonar.projectKey", "${org}_gradle-plugins")
		property("sonar.organization", "Kord-Extensions")
		property("sonar.projectVersion", rootProject.version.toString())
		property("sonar.host.url", System.getenv()["SONAR_HOST_URL"] ?: "")
		property("sonar.token", System.getenv()["SONAR_TOKEN"] ?: "" )
		property("sonar.scm.provider", "git")
		property("sonar.coverage.jacoco.xmlReportPaths", "build/reports/kover/report.xml")

		property("sonar.links.homepage", homepageUrl)
		property("sonar.links.ci", "$gitUrl/actions")
		property("sonar.links.scm", gitUrl)
		property("sonar.links.issue", "$gitUrl/issues")
	}
}

subprojects {
	this.group = "dev.kordex.gradle.plugins"
	this.version = projectVersion
}
