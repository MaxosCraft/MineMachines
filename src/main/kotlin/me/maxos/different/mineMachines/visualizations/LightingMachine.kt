package me.maxos.different.mineMachines.visualizations

import me.maxos.different.mineMachines.MineMachines
import me.maxos.different.mineMachines.containers.MachinesContainer
import me.maxos.different.mineMachines.files.configs.ConfigManager
import me.maxos.different.mineMachines.model.machines.InstallMachine
import me.maxos.different.mineMachines.utils.logInfo
import me.maxos.different.mineMachines.visualizations.scoreboard.ScoreBoardTeam
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.EntityType
import org.bukkit.entity.Shulker
import org.bukkit.persistence.PersistentDataType
import java.util.UUID

class LightingMachine(

	private val plugin: MineMachines,
	private val scoreBoardTeam: ScoreBoardTeam,
	private val configManager: ConfigManager

) {
/*
	fun deepClear() {
		for (world in Bukkit.getWorlds()) {
			for (entity in world.entities) {
				if (!entity.persistentDataContainer.has(plugin.glowShulkerKey)) continue
				entity.remove()
			}
		}
	}

 */

	fun spawnShulker(location: Location, installMachine: InstallMachine) {

		val id = installMachine.typeId

		val color = configManager.getAllMachines()[id]?.color

		if (color?.lowercase() == "none" || color == null) return

		val shulker = location.world.spawnEntity(location, EntityType.SHULKER) as Shulker

		shulker.peek = 0.0F
		shulker.isAware = false
		shulker.isCollidable = false
		shulker.isGliding = false
		shulker.isInvulnerable = true
		shulker.isInvisible = true

		shulker.setAI(false)
		shulker.setGravity(false)

		shulker.isSilent = true
		shulker.isPersistent = true

		shulker.persistentDataContainer.set(plugin.glowShulkerKey, PersistentDataType.STRING, "lighting")

		scoreBoardTeam.addEntityToTeam(id, shulker)
		shulker.isGlowing = true

	//	logInfo(shulker.location.toString())

		installMachine.shulker = shulker.uniqueId

	}

	fun killShulker(uuid: UUID) {
		Bukkit.getEntity(uuid)?.remove()
	}

	fun cleanupAllShulkers(machines: MutableCollection<InstallMachine>) {
		for (machine in machines) {
			val uuid = machine.shulker
			if (uuid != null) killShulker(uuid)
		}
	}

}