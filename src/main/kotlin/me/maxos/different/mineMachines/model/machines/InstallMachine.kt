package me.maxos.different.mineMachines.model.machines

import me.maxos.different.mineMachines.model.videocard.VideoCard
import net.j4c0b3y.api.menu.Menu
import org.bukkit.scheduler.BukkitTask
import java.util.UUID

data class InstallMachine (

	val typeId: String,
	var balance: Double,
	var cashProfits: Double,
	var gpuAmount: Int,
	var activeGpu: Int,
	val owner: UUID,
	var scheduler: BukkitTask?,

	val world: String,
	val x: Int,
	val y: Int,
	val z: Int,

	val cards: MutableList<VideoCard>?,

	var activeMenu: Menu?,

	var shulker: UUID?

)