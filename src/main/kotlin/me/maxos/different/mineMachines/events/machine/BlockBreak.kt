package me.maxos.different.mineMachines.events.machine

import me.maxos.different.mineMachines.containers.MachinesContainer
import me.maxos.different.mineMachines.files.configs.ConfigManager
import me.maxos.different.mineMachines.files.configs.MessagesManager
import me.maxos.different.mineMachines.items.videocards.GpuItemStack
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent

class BlockBreak(

	private val machinesContainer: MachinesContainer,
	private val gpuItemStack: GpuItemStack,
	private val configManager: ConfigManager,
	private val messagesManager: MessagesManager

): Listener {

	@EventHandler(ignoreCancelled = true)
	fun machineBreak(e: BlockBreakEvent) {

		val block = e.block
		val material = block.type
		val player = e.player
		val location = block.location.clone()
		val world = location.world

		if (material !in configManager.machinesMaterials) return
		if (!machinesContainer.hasLocationMachine(location)) return

		e.isDropItems = false

		val itemStack = machinesContainer.getItemStack(location)

		world.dropItem(location, itemStack)

		player.sendMessage(messagesManager.getMessage("break", "§cМайнинг-ферма сломана!"))

		val machine = machinesContainer.getMachine(location)

		if (machine?.activeMenu != null) {

			val machineMenu = machine.activeMenu

			machineMenu?.close()
			machineMenu?.player?.sendMessage(messagesManager.getMessage("break-menu", "§cМайнинг-ферма сломана! Меню закрыто!"))

		}

		val cards = machine?.cards
		cards?.forEach { card ->
			val cardItem = gpuItemStack.getItemStack(card.id)
			cardItem?.let {
				world.dropItem(location, cardItem)
			}
		}

		machinesContainer.removeMachine(location)

	}

}