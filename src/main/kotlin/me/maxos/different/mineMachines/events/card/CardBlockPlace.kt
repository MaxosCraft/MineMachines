package me.maxos.different.mineMachines.events.card

import me.maxos.different.mineMachines.files.configs.ConfigManager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent

class CardBlockPlace(

	private val configManager: ConfigManager

): Listener {

	@EventHandler(ignoreCancelled = true)
	fun cardPlace(e: BlockPlaceEvent) {

		val block = e.blockPlaced
		val material = block.type

		if (material !in configManager.cardsMaterials) {
			return
		} else e.isCancelled = true

	}

}