package me.maxos.different.mineMachines.menu

import me.maxos.different.mineMachines.MineMachines
import me.maxos.different.mineMachines.containers.MachinesContainer
import me.maxos.different.mineMachines.files.configs.menu.MenuConfigManager
import me.maxos.different.mineMachines.menu.buttons.CardButton
import me.maxos.different.mineMachines.menu.buttons.action.ClickButton
import me.maxos.different.mineMachines.menu.template.CardTemplate
import me.maxos.different.mineMachines.model.machines.InstallMachine
import me.maxos.different.mineMachines.utils.logInfo
import net.j4c0b3y.api.menu.Menu
import net.j4c0b3y.api.menu.MenuSize
import net.j4c0b3y.api.menu.button.Button
import net.j4c0b3y.api.menu.layer.impl.BackgroundLayer
import net.j4c0b3y.api.menu.layer.impl.ForegroundLayer
import net.j4c0b3y.api.menu.pagination.PaginatedMenu
import net.j4c0b3y.api.menu.pagination.PaginationSlot
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

class CardsMenu(

	private val plugin: MineMachines,
	private val items: List<ItemStack>,
	private val player: Player,
	private val installMachine: InstallMachine,
	private val homeMenu: Menu,
	private val machinesContainer: MachinesContainer,
	private val menuConfigManager: MenuConfigManager,
	private val clickButton: ClickButton,
	private val location: Location

) : PaginatedMenu(menuConfigManager.titleCardMenu, MenuSize.valueOf(menuConfigManager.sizeCardMenu ?: "FIVE"), player) {

	private var currentItems: MutableList<ItemStack> = items.toMutableList()

	override fun setup(background: BackgroundLayer, foreground: ForegroundLayer) {

		apply(CardTemplate(
			this,
			menuConfigManager,
			clickButton,
			plugin,
			installMachine,
			items,
			this,
			currentItems
		))

		if (menuConfigManager.rangeSlotsCards != null) {
			for (i in menuConfigManager.rangeSlotsCards!!) {
				foreground.set(i, PaginationSlot(this))
			}
		} else centers()

	}

	override fun getEntries(): List<Button> {

		val buttons = mutableListOf<Button>()

		currentItems.forEach { item ->

			buttons.add(CardButton(
				item,
				installMachine,
				onClickUnit = {

					currentItems.remove(item)

					val cardKey = item.itemMeta.persistentDataContainer.get(plugin.gpuKey, PersistentDataType.STRING) ?: return@CardButton
					machinesContainer.pickupCard(
						installMachine.world,
						installMachine.x,
						installMachine.y,
						installMachine.z,
						cardKey
					)
					refresh()

					player.inventory.addItem(item)

				}))
		}

		return buttons
	}

	private fun centers() {
		foreground.center(PaginationSlot(this))
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