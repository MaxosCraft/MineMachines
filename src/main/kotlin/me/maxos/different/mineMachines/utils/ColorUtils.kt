package me.maxos.different.mineMachines.utils

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer

object ColorUtils {

	private val legacySerializer = LegacyComponentSerializer.builder()
		.hexColors()
		.useUnusualXRepeatedCharacterHexFormat()
		.build()

	fun colorize(message: String): String {
		return legacySerializer.serialize(LegacyComponentSerializer.legacyAmpersand().deserialize(message))
	}

	fun colorizeList(messages: List<String>): List<String> {
		return messages.map { colorize(it) }
	}

}
