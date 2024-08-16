package template

fun main() {
	val devMode = System.getProperties().contains("devMode")

	println("Main class!")

	if (devMode) {
		println("Dev mode!")
	}

	println("Env vars: " + System.getenv().keys.sorted().joinToString())
	println("Overridden var: " + System.getenv("Z_OVERRIDDEN"))
}
