package me.maxos.different.mineMachines.events.machine

import me.maxos.different.mineMachines.containers.MachinesContainer
import me.maxos.different.mineMachines.files.configs.ConfigManager
import me.maxos.different.mineMachines.files.configs.MessagesManager
import me.maxos.different.mineMachines.items.videocards.GpuItemStack
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockExplodeEvent
import org.bukkit.event.entity.EntityExplodeEvent

class BlockExplodes(

	private val machinesContainer: MachinesContainer,
	private val configManager: ConfigManager,
	private val gpuItemStack: GpuItemStack,
	private val messagesManager: MessagesManager

): Listener {

	@EventHandler(ignoreCancelled = true)
	fun machineExplodesBlock(e: BlockExplodeEvent) {
		handleExplosion(e.blockList())
	}


	@EventHandler(ignoreCancelled = true)
	fun machineExplodesEntity(e: EntityExplodeEvent) {
		handleExplosion(e.blockList())
	}

	private fun handleExplosion(blockList: MutableList<Block>) {

		val explodeStatus = configManager.explodeStatus ?: true

		when (explodeStatus) {
			true -> blockList.removeIf { block: Block ->
				val location = block.location.clone()
				machinesContainer.hasLocationMachine(location)
			}

			false -> {
				val machines = blockList.filter { block ->
					val location = block.location.clone()
					machinesContainer.hasLocationMachine(location)
				}
				machines.forEach {
					val location = it.location.clone()
					val machine = machinesContainer.getMachine(location)
					val item = machinesContainer.getItemStack(location)
					val menu = machine?.activeMenu
					menu?.close()
					menu?.player?.sendMessage(messagesManager.getMessage("break-menu", "§cМайнинг-ферма была сломана! Меню закрыто!"))

					val cards = machine?.cards
					cards?.forEach { card ->
						val cardItem = gpuItemStack.getItemStack(card.id)
						cardItem?.let {
							location.world.dropItem(location, cardItem)
						}
					}

					location.world.dropItem(location, item)

					it.type = Material.AIR

					machinesContainer.removeMachine(location)

				}


			}

		}

	}

}

