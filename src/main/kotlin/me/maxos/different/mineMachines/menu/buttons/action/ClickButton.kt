package me.maxos.different.mineMachines.menu.buttons.action

import me.maxos.different.mineMachines.MineMachines
import me.maxos.different.mineMachines.containers.MachinesContainer
import me.maxos.different.mineMachines.files.configs.ConfigManager
import me.maxos.different.mineMachines.files.configs.MessagesManager
import me.maxos.different.mineMachines.files.configs.menu.MenuConfigManager
import me.maxos.different.mineMachines.menu.MachineMenu
import me.maxos.different.mineMachines.menu.CardsMenu
import me.maxos.different.mineMachines.menu.buttons.RequiredButton
import me.maxos.different.mineMachines.model.machines.InstallMachine
import net.j4c0b3y.api.menu.Menu
import net.j4c0b3y.api.menu.button.ButtonClick
import net.j4c0b3y.api.menu.pagination.PaginatedMenu
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.inventory.ItemStack

class ClickButton(

	private val configManager: ConfigManager,
	private val plugin: MineMachines,
	private val machinesContainer: MachinesContainer,
	private val menuConfigManager: MenuConfigManager,
	private val messagesManager: MessagesManager

) {

	fun clickAction(
		button: RequiredButton?, click: ButtonClick, installMachine: InstallMachine,
		items: List<ItemStack>, homeMenu: Menu, paginatedMenu: PaginatedMenu?,
		clickButton: ClickButton?, currentItems: MutableList<ItemStack>?) {

		when (button) {
			RequiredButton.INFO -> {}
			RequiredButton.BALANCE -> buttonBalance(click, installMachine)
			RequiredButton.VIDEOCARDS -> buttonVideoCards(click, installMachine, items, homeMenu)
			RequiredButton.NEXT -> paginatedMenu?.nextPage()
			RequiredButton.BACK -> paginatedMenu?.previousPage()
			RequiredButton.HOME -> buttonHome(
				click,
				paginatedMenu,
				clickButton,
				installMachine,
				currentItems,
			)
			else -> {}
		}

	}
/*
	private fun buttonInfo(click: ButtonClick) {

		val player = click.menu.player

	}

 */

	private fun buttonHome(
		click: ButtonClick, paginatedMenu: PaginatedMenu?,
		clickButton: ClickButton?, installMachine: InstallMachine?,
		currentItems: MutableList<ItemStack>?) {

		val player = click.menu.player
		if (installMachine != null && clickButton != null && currentItems != null) {
			val location = Location(
				Bukkit.getWorld(installMachine.world),
				installMachine.x.toDouble(),
				installMachine.y.toDouble(),
				installMachine.z.toDouble()
			)
			val newMachine = machinesContainer.getMachine(
				location
			)
			paginatedMenu?.close()
			if (newMachine != null)
				MachineMenu(
					player, menuConfigManager,
					clickButton,
					plugin,
					newMachine,
					currentItems,
					machinesContainer,
					location,
				).open()
		}
	}

	private fun buttonBalance(click: ButtonClick, installMachine: InstallMachine) {

		val player = click.menu.player

		val location = Location(
			Bukkit.getWorld(installMachine.world),
			installMachine.x.toDouble(),
			installMachine.y.toDouble(),
			installMachine.z.toDouble()
		)

		val owner = machinesContainer.getOwner(location)
		if (owner != null) {
			plugin.server.dispatchCommand(
				Bukkit.getConsoleSender(), configManager.commandGiveBalance!!
					.replace("{balance}", machinesContainer.getBalance(location).toString())
					.replace("{player}", owner.name)
			)
			machinesContainer.resetBalance(location)

			player.sendMessage(messagesManager.getMessage("collect", "§aВы забрали прибыль!"))

			click.menu.close()
		}
	}

	private fun buttonVideoCards(click: ButtonClick, installMachine: InstallMachine, items: List<ItemStack>, homeMenu: Menu) {

		val player = click.menu.player

		val location = Location(
			Bukkit.getWorld(installMachine.world),
			installMachine.x.toDouble(),
			installMachine.y.toDouble(),
			installMachine.z.toDouble()
		)

		val updateMachine = machinesContainer.getMachine(location) ?: return

		val menu = CardsMenu(
			plugin,
			items,
			player,
			updateMachine,
			homeMenu,
			machinesContainer,
			menuConfigManager,
			this,
			location
		)

		menu.open()

	}

}