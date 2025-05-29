package template

import dev.kordex.core.ExtensibleBot
import dev.kordex.core.utils.envOrNull

suspend fun main() {
	val bot = ExtensibleBot(envOrNull("TOKEN") ?: "FAKE") {

	}

	bot.start()
}
