package me.maxos.different.mineMachines.files.configs.menu

import me.maxos.different.mineMachines.files.FileManager
import me.maxos.different.mineMachines.menu.buttons.RequiredButton
import me.maxos.different.mineMachines.model.buttons.NewButton
import me.maxos.different.mineMachines.utils.ColorUtils
import me.maxos.different.mineMachines.utils.logInfo
import me.maxos.different.mineMachines.utils.logWarn
import org.bukkit.Material
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.FileConfiguration
import java.util.concurrent.ConcurrentHashMap


class MenuConfigManager(

	private val menuSettingsManager: FileManager

) {

	private var settings: FileConfiguration = menuSettingsManager.getConfig()

	var titleMainMenu: String? = null
	var titleCardMenu: String? = null
	var sizeMainMenu: String? = null
	var sizeCardMenu: String? = null

	var rangeSlotsCards: List<Int>? = null

	val mapRequiredButtons = ConcurrentHashMap<String, NewButton>()
	private var requiredMainItems = settings.getConfigurationSection("main-menu.required-items")
	private var requiredCardItems = settings.getConfigurationSection("videocards-menu.control-buttons")

	companion object {
		private val REQUIRED_BUTTONS = RequiredButton.values().map { it.id }
	}

	val decorNewButtonsMain = mutableListOf<NewButton>()
	val decorNewButtonsCard = mutableListOf<NewButton>()
	private var decorationItemsMain = settings.getConfigurationSection("main-menu.decoration-items")
	private var decorationItemsCard = settings.getConfigurationSection("videocards-menu.decoration-items")

	init {
		initMenu()
		/*
		logInfo("Обязательные предметы: " + this.mapRequiredButtons.toString())
		logInfo("Декор предметы: " + this.decorationItemsMain.toString())
		 */
	}

	fun reloadMenus() {
		settings = menuSettingsManager.getConfig()
		requiredMainItems = settings.getConfigurationSection("main-menu.required-items") ?: requiredMainItems
		requiredCardItems = settings.getConfigurationSection("videocards-menu.control-buttons") ?: requiredCardItems
		decorationItemsMain = settings.getConfigurationSection("main-menu.decoration-items") ?: decorationItemsMain
		decorationItemsCard = settings.getConfigurationSection("videocards-menu.decoration-items") ?: decorationItemsCard
		decorNewButtonsMain.clear()
		decorNewButtonsCard.clear()
		initMenu()
	}

	private fun initMenu() {

		createRequiredButtons(requiredMainItems, requiredCardItems)
		createDecorButtons()

		titleMainMenu = ColorUtils.colorize(settings.getString("main-menu.title") ?: "Главное меню")
		titleCardMenu = ColorUtils.colorize(settings.getString("videocards-menu.title") ?: "Меню видеокарт")

		sizeMainMenu = settings.getString("main-menu.size")?.uppercase()
		sizeCardMenu = settings.getString("videocards-menu.size")?.uppercase()

		rangeSlotsCards = settings.getIntegerList("videocards-menu.cards-slots")

	}

	private fun createRequiredButtons(mainSection: ConfigurationSection?, cardSection: ConfigurationSection?) {

		val mainButtons = mainSection?.getKeys(false) ?: emptySet()
		val cardButtons = cardSection?.getKeys(false) ?: emptySet()

		val allReqButtons = mainButtons + cardButtons

		if (!allReqButtons.containsAll(REQUIRED_BUTTONS)) {
			val missing = REQUIRED_BUTTONS - allReqButtons
			logWarn("Обязательные кнопки для меню были повреждены! Отсутствуют: $missing")
			return
		}

		mainButtons.forEach { item ->
			val buttonSettings = mainSection?.getConfigurationSection(item)
			mapRequiredButtons[item] = initButtonObject(buttonSettings)
		}

		cardButtons.forEach { item ->
			val buttonSettings = cardSection?.getConfigurationSection(item)
			mapRequiredButtons[item] = initButtonObject(buttonSettings)
		}

	}


	private fun createDecorButtons() {

		decorationItemsMain?.getKeys(false)?.forEach { item ->
			val buttonSettings = decorationItemsMain?.getConfigurationSection(item)
			decorNewButtonsMain.add(initButtonObject(buttonSettings))
		}

		decorationItemsCard?.getKeys(false)?.forEach { item ->
			val buttonSettings = decorationItemsCard?.getConfigurationSection(item)
			decorNewButtonsCard.add(initButtonObject(buttonSettings))
		}

	}

	private fun initButtonObject(configurationSection: ConfigurationSection?): NewButton {

		if (configurationSection != null) {

			val newButton = NewButton(

				try {
					Material.valueOf(configurationSection.getString("material").toString())
				} catch (e: Exception) {
					Material.GRAY_STAINED_GLASS_PANE
				},

				ColorUtils.colorize(configurationSection.getString("name").toString()),
				ColorUtils.colorizeList(configurationSection.getStringList("lore")),
				configurationSection.getIntegerList("slots")

			)

			return newButton

		} else return wrongNewButton

	}

	private val wrongNewButton = NewButton(
		Material.BARRIER,
		"§cПроверьте настройки в menu.yml !!!",
		listOf(""),
		listOf(0)
	)

}