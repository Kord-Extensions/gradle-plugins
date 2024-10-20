package template

import dev.kord.core.entity.interaction.OptionValue
import dev.kordex.core.annotations.converters.Converter
import dev.kordex.core.annotations.converters.ConverterType
import dev.kordex.core.commands.Argument
import dev.kordex.core.commands.CommandContext
import dev.kordex.core.commands.OptionWrapper
import dev.kordex.core.commands.converters.SingleConverter
import dev.kordex.core.commands.converters.Validator
import dev.kordex.core.i18n.types.Key
import dev.kordex.parser.StringParser

@Converter(
	names = ["dummy"],
	types = [ConverterType.SINGLE]
)
class DummyConverter(
	override var validator: Validator<String> = null,
) : SingleConverter<String>() {
	override val signatureType: Key
		get() = TODO("Not yet implemented")

	override suspend fun parse(
		parser: StringParser?,
		context: CommandContext,
		named: String?
	): Boolean {
		TODO("Not yet implemented")
	}

	override suspend fun toSlashOption(arg: Argument<*>): OptionWrapper<*> {
		TODO("Not yet implemented")
	}

	override suspend fun parseOption(
		context: CommandContext,
		option: OptionValue<*>
	): Boolean {
		TODO("Not yet implemented")
	}
}
