package me.maxos.different.mineMachines.files

import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger

class FileManager(

	private val plugin: JavaPlugin,
	private val fileName: String) {

	private var сonfig: FileConfiguration? = null
	private var configFile: File? = null


	init {
		saveDefaultConfig()
		reloadConfig()
	}

	// Сохраняем дефолтный конфиг
	private fun saveDefaultConfig() {
		if (configFile == null) {
			configFile = File(plugin.dataFolder, fileName)
		}

		if (!configFile!!.exists()) {
			plugin.logger.info("Создание $fileName...")
			plugin.saveResource(fileName, false)
			plugin.logger.info("Создан файл $fileName")
		}
	}

	// Перезагрузка конфига
	fun reloadConfig() {
		if (configFile == null) {
			configFile = File(plugin.dataFolder, fileName)
		}

		сonfig = YamlConfiguration.loadConfiguration(configFile!!)

		if (сonfig!!.getKeys(false).isEmpty()) {
			Logger.getLogger("ERROR").severe("\n\n\nВаш файл $fileName повреждён!\n\n\n")
		}

		plugin.getResource(fileName)?.let { defaultStream ->
			val defaultConfig = YamlConfiguration.loadConfiguration(
				InputStreamReader(defaultStream, StandardCharsets.UTF_8)
			)
			сonfig!!.setDefaults(defaultConfig)
		}
	}
	// Получение конфига
	fun getConfig(): FileConfiguration {
		if (сonfig == null) {
			reloadConfig()
		}
		return сonfig!!
	}

	// Сохранение конфига
	fun saveConfig() {
		if (сonfig == null || configFile == null) {
			return
		}

		try {
			getConfig().save(configFile!!)
		} catch (ex: IOException) {
			plugin.logger.log(Level.SEVERE, "Не удалось сохранить $fileName", ex)
		}
	}

	fun getPlayer(uuid: UUID) : Int {
		return if (getConfig().contains("data.$uuid")) {
			getConfig().getInt("data.$uuid")
		} else 0
	}

	fun addPlayer(uuid: UUID) {
		getConfig().set("data.$uuid",
			(getConfig().getInt("data.$uuid", 0) + 1)
		)
		saveConfig()
	}

	fun setPlayer(uuid: UUID, value: Int) {
		getConfig().set("data.$uuid", value)
		saveConfig()
	}

}