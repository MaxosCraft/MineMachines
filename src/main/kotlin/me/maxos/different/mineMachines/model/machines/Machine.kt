package me.maxos.different.mineMachines.model.machines

import me.maxos.different.mineMachines.utils.logInfo
import org.bukkit.Material

data class Machine(

	val id: String,
	val name: String,
	val lore: List<String>,
	val material: Material,
	val cashProfits: Double,
	val gpuAmount: Int,
	val glow: Boolean,
	val color: String?

) {

	init {
		logInfo("Машина $id($name) успешно создана!")
	}

}