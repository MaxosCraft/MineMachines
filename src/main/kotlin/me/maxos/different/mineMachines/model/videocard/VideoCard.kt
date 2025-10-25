package me.maxos.different.mineMachines.model.videocard

import me.maxos.different.mineMachines.utils.logInfo
import org.bukkit.Material

data class VideoCard (

	val id: String,
	val name: String,
	val lore: List<String>,
	val material: Material,
	var profitMargin: Double,
	val glow: Boolean

) {

	init {
		logInfo("Видеокарта $id($name) успешно создана!")
	}

}