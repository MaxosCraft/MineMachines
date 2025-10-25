package me.maxos.different.mineMachines.menu

import me.maxos.different.mineMachines.MineMachines
import me.maxos.different.mineMachines.containers.MachinesContainer
import me.maxos.different.mineMachines.files.configs.menu.MenuConfigManager
import me.maxos.different.mineMachines.model.machines.InstallMachine
import me.maxos.different.mineMachines.menu.buttons.action.ClickButton
import me.maxos.different.mineMachines.menu.template.MenuTemplate
import me.maxos.different.mineMachines.utils.logInfo
import net.j4c0b3y.api.menu.Menu
import net.j4c0b3y.api.menu.MenuSize
import net.j4c0b3y.api.menu.layer.impl.BackgroundLayer
import net.j4c0b3y.api.menu.layer.impl.ForegroundLayer
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class MachineMenu(

	player: Player,
	private val menuConfigManager: MenuConfigManager,
	private val clickButton: ClickButton,
	private val plugin: MineMachines,
	private val installMachine: InstallMachine,
	private val items: List<ItemStack>,
	private val machinesContainer: MachinesContainer,
	private val location: Location

) : Menu(menuConfigManager.titleMainMenu ?: "Майнинг-ферма", MenuSize.valueOf(menuConfigManager.sizeMainMenu ?: "FIVE"), player) {

	override fun setup(background: BackgroundLayer?, foreground: ForegroundLayer) {
		apply(MenuTemplate(this, menuConfigManager, clickButton,
			plugin, installMachine, items, machinesContainer))
	}


	override fun onOpen() {
		super.onOpen()
	//	logInfo("Меню открылось!")
		machinesContainer.settingStatusMenu(location, this)
	}

	override fun onClose() {
		super.onClose()
	//	logInfo("Меню закрылось!")
		machinesContainer.settingStatusMenu(location, null)
	}

}