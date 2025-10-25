package me.maxos.different.mineMachines.model.buttons

import org.bukkit.Material

data class NewButton (

	val material: Material,
	val name: String,
	val lore: List<String>,
	val slots: List<Int>

)