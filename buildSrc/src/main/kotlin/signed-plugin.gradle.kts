plugins {
	signing
}

signing {
	val signingKey: String? by project ?: return@signing
	val signingPassword: String? by project ?: return@signing

	useInMemoryPgpKeys(signingKey, signingPassword)
}
