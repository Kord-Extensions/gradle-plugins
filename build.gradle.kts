val projectVersion: String by project

group = "dev.kordex.gradle.plugins"
version = projectVersion

subprojects {
	this.group = "dev.kordex.gradle.plugins"
	this.version = projectVersion
}
