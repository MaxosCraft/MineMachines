package me.maxos.different.mineMachines.events.entity

import me.maxos.different.mineMachines.MineMachines
import org.bukkit.entity.EntityType
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent

class ShulkerDamage(

	private val plugin: MineMachines

): Listener {

	@EventHandler
	fun onDamageShulker(e: EntityDamageByEntityEvent) {

		val entity = e.entity

		if (entity.type != EntityType.SHULKER) return
		if (!entity.persistentDataContainer.has(plugin.glowShulkerKey)) return

		e.isCancelled = true

	}

}