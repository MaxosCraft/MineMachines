package me.maxos.different.mineMachines.events.machine.actions

import me.maxos.different.mineMachines.containers.MachinesContainer
import me.maxos.different.mineMachines.files.configs.ConfigManager
import org.bukkit.block.Block
import org.bukkit.event.Cancellable
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPistonExtendEvent
import org.bukkit.event.block.BlockPistonRetractEvent

class BanActionsBlock(

	private val machinesContainer: MachinesContainer,
	private val configManager: ConfigManager

): Listener {

	@EventHandler(ignoreCancelled = true)
	fun machineExtendPiston(e: BlockPistonExtendEvent) {
		handlePistonEvent(e.blocks, e.block, e)
	}

	@EventHandler(ignoreCancelled = true)
	fun machineRetractPiston(e: BlockPistonRetractEvent) {
		handlePistonEvent(e.blocks, e.block, e)
	}

	private fun handlePistonEvent(blocks: List<Block>, piston: Block, event: Cancellable) {
		for (block in blocks) {
			if (block.type !in configManager.machinesMaterials) continue

			if (machinesContainer.hasLocationMachine(block.location.clone())) {
				if (configManager.destroyPiston != false) {
					piston.breakNaturally()
				}
				event.isCancelled = true
				break
			}
		}
	}

}