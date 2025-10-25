package me.maxos.different.mineMachines.files.configs

import me.maxos.different.mineMachines.files.FileManager
import me.maxos.different.mineMachines.utils.ColorUtils
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.FileConfiguration
import java.util.concurrent.ConcurrentHashMap

class MessagesManager(

	private val messagesSettingsManager: FileManager

) {

	private var settings: FileConfiguration = messagesSettingsManager.getConfig()

	private val allSectionsMessages = mutableListOf<ConfigurationSection>()
	private val readyMessages = ConcurrentHashMap<String, String>()

	init {
		getAllSections()
		completion()
	}

	fun reloadConfig() {
		settings = messagesSettingsManager.getConfig()
		allSectionsMessages.clear()
		readyMessages.clear()
		getAllSections()
		completion()
	}

	fun getMessage(value: String, defaultValue: String): String {
		return readyMessages[value] ?: defaultValue
	}

	private fun getAllSections() {
		settings.getKeys(false).forEach {
			val section = settings.getConfigurationSection(it)
			if (section != null) allSectionsMessages.add(section)
		}
	}

	private fun completion() {
		for (i in allSectionsMessages) {
			coloring(i)
		}
	}

	private fun coloring(configurationSection: ConfigurationSection) {
		val keys = configurationSection.getKeys(false)
		for (key in keys) {

			when (val value = configurationSection.get(key)) {

				is List<*> -> {
					val strings = value.filterIsInstance<String>()
					val message = strings.joinToString("\n")
					readyMessages[key] = ColorUtils.colorize(message)
				}

				is String -> {
					readyMessages[key] = ColorUtils.colorize(value)
				}

			}

		}

	}

}