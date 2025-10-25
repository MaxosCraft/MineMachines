package me.maxos.different.mineMachines.files.configs

import me.maxos.different.mineMachines.files.FileManager
import me.maxos.different.mineMachines.model.machines.Machine
import me.maxos.different.mineMachines.model.videocard.VideoCard
import org.bukkit.Material
import org.bukkit.configuration.file.FileConfiguration
import me.maxos.different.mineMachines.utils.ColorUtils.colorize
import me.maxos.different.mineMachines.utils.ColorUtils.colorizeList
import java.util.concurrent.ConcurrentHashMap


class ConfigManager(

	private val settingsManager: FileManager

) {

	private var settings: FileConfiguration = settingsManager.getConfig()


	var destroyPiston: Boolean? = null
	var dbSaveInterval: Int? = null
	var explodeStatus: Boolean? = null
	var strictOwnership: Boolean? = null
	var commandGiveBalance: String? = null

	var machinesMaterials: MutableSet<Material> = mutableSetOf()
	var cardsMaterials: MutableSet<Material> = mutableSetOf()

	private var machinesConfig = settings.getConfigurationSection("machines")
	private var videoCardsConfig = settings.getConfigurationSection("video-cards")

	private var machinesModels = ConcurrentHashMap<String, Machine>()
	private var videoCards = ConcurrentHashMap<String, VideoCard>()

	init {
		initMainSettings()
	}

	private fun initMainSettings() {
		createMachines()
		createVideoCards()
		setParameters()
	}

	fun reloadMainSettings() {
		settings = settingsManager.getConfig()
		machinesConfig = settings.getConfigurationSection("machines") ?: machinesConfig
		videoCardsConfig = settings.getConfigurationSection("video-cards") ?: videoCardsConfig
		machinesModels.clear()
		videoCards.clear()
		machinesMaterials.clear()
		cardsMaterials.clear()
		initMainSettings()
	}

	private fun setParameters() {

		destroyPiston = settings.getBoolean("destroy-pistons", true)
		explodeStatus = settings.getBoolean("anti-explode", true)
		dbSaveInterval = settings.getInt("db-save-interval") * 1200 // минуты в тики конвертим
		strictOwnership = settings.getBoolean("strict-ownership", true)
		commandGiveBalance = settings.getString("command-give-balance", "eco give {balance}")

	}

	private fun createMachines() {

		for (id in machinesConfig!!.getKeys(false)) {

			val machineSettings = machinesConfig?.getConfigurationSection(id)

			if (machineSettings != null) {

				val material = Material.valueOf(
					machineSettings.getString("material") ?: "SPAWNER"
				)

				val machine = Machine(
					id,
					colorize(machineSettings.getString("name") ?: "§eМайнинг-Ферма"),
					colorizeList(machineSettings.getStringList("lore")),
					try {
						material
					} catch (e: Exception) {
						Material.SPAWNER
					},
					machineSettings.getInt("income").toDouble(),
					machineSettings.getInt("gpu-amount"),
					machineSettings.getBoolean("glow"),
					machineSettings.getString("lighting-color")
				)

				machinesModels[id] = machine

				machinesMaterials.add(material)

			}

		}

	}

	fun getAllMachines() = machinesModels

	private fun createVideoCards() {

		for (id in videoCardsConfig!!.getKeys(false)) {

			val gpuSettings = videoCardsConfig?.getConfigurationSection(id)

			if (gpuSettings != null) {

				val material = Material.valueOf(
					gpuSettings.getString("material") ?: "IRON_INGOT"
				)

				val card = VideoCard(
					id,
					colorize(gpuSettings.getString("name") ?: "§eВидеокарта"),
					colorizeList(gpuSettings.getStringList("lore")),
					try {
						material
					} catch (e: Exception) {
						Material.IRON_INGOT
					},
					gpuSettings.getInt("profit-margin").toDouble(),
					gpuSettings.getBoolean("glow")
				)

				videoCards[id] = card

				cardsMaterials.add(material)

			}

		}

	}

	fun getAllGpu() = videoCards

	fun getGpu(id: String): VideoCard? {
		videoCards.values.forEach {
			if (it.id == id) return it
		}
		return null
	}



}