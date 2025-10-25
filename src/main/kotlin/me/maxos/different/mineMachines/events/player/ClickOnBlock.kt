package me.maxos.different.mineMachines.events.player

import me.maxos.different.mineMachines.MineMachines
import me.maxos.different.mineMachines.containers.MachinesContainer
import me.maxos.different.mineMachines.database.DatabaseManager
import me.maxos.different.mineMachines.files.configs.ConfigManager
import me.maxos.different.mineMachines.files.configs.MessagesManager
import me.maxos.different.mineMachines.files.configs.menu.MenuConfigManager
import me.maxos.different.mineMachines.items.videocards.GpuItemStack
import me.maxos.different.mineMachines.menu.MachineMenu
import me.maxos.different.mineMachines.menu.buttons.action.ClickButton
import me.maxos.different.mineMachines.utils.logInfo
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

class ClickOnBlock(

	private val menuConfigManager: MenuConfigManager,
	private val clickButton: ClickButton,
	private val machinesContainer: MachinesContainer,
	private val plugin: MineMachines,
	private val configManager: ConfigManager,
	private val databaseManager: DatabaseManager,
	private val gpuItemStack: GpuItemStack,
	private val messagesManager: MessagesManager

): Listener {

	@EventHandler(ignoreCancelled = true)
	fun clickOnMachine(e: PlayerInteractEvent) {

		if (!e.action.isRightClick) return
		if (e.clickedBlock == null) return
		if (e.hand != EquipmentSlot.HAND) return

		val block = e.clickedBlock!!
		val material = block.type
		val location = block.location.clone()
		val player = e.player
		val uuid = player.uniqueId

		if (material !in configManager.machinesMaterials) return

		if (!machinesContainer.hasLocationMachine(location)) return

		e.isCancelled = true

		val machine = machinesContainer.getMachine(location) ?: return

		if (machine.activeMenu != null) {
			player.sendMessage(messagesManager.getMessage("occupied", "§cМайнинг-ферма занята другим игроком!"))
			return
		}

		if (configManager.strictOwnership == true && machine.owner != uuid) {
			player.sendMessage(messagesManager.getMessage("stranger", "§cЭто чужая майнинг-ферма! У вас нету доступа!"))
			return
		}

		val item = player.inventory.itemInMainHand

		if (item.type != Material.AIR && item.itemMeta != null) {

			val itemPdc = item.itemMeta.persistentDataContainer
			if (itemPdc.has(plugin.gpuKey)) {

				val key = itemPdc.get(plugin.gpuKey, PersistentDataType.STRING) ?: return
				var amount = machine.activeGpu
				if (amount < machine.gpuAmount) {

					val card = configManager.getGpu(key) ?: return

					item.amount--

					machinesContainer.updateMachineCards(location, card)
					amount += 1

					databaseManager.saveGpuInMachine(
						machine.world,
						machine.x,
						machine.y,
						machine.z,
						key,
						amount
					)

					player.sendMessage(messagesManager.getMessage("insert-gpu", "§aВы успешно вставили видеокарту!"))

				} else player.sendMessage(messagesManager.getMessage("no-free-slot", "§cВ майнинг-ферме не осталось слотов под видеокарты!"))

				return
			}
		}

		val items = mutableListOf<ItemStack>()

		if (machine.cards != null) {
			for (i in machine.cards) {

				if (gpuItemStack.getItemStack(i.id) != null) {
					items.add(gpuItemStack.getItemStack(i.id)!!)
				}
			}
		}

		val menu = MachineMenu(player, menuConfigManager, clickButton, plugin, machine, items, machinesContainer, location)
		menu.open()
	//	logInfo(menu.toString())
	//	logInfo(menu.handler.toString())
	//	logInfo(menu.handler.openMenus[player].toString())

	}


}