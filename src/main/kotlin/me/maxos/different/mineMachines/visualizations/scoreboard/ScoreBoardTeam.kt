package me.maxos.different.mineMachines.visualizations.scoreboard

import me.maxos.different.mineMachines.MineMachines
import me.maxos.different.mineMachines.files.configs.ConfigManager
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.NamedTextColor.DARK_GRAY
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Color
import org.bukkit.entity.Entity
import org.bukkit.scoreboard.NameTagVisibility
import org.bukkit.scoreboard.Team
import java.util.concurrent.ConcurrentHashMap

class ScoreBoardTeam(

	private val plugin: MineMachines,
	private val teamName: String,
	private val configManager: ConfigManager

) {

	private val scoreboard = plugin.server.scoreboardManager.mainScoreboard

	private val coloredTeams = ConcurrentHashMap<String, Team>()

	fun add(id: String, entity: Entity) {
		coloredTeams[id]?.addEntity(entity)
	}

	fun reloadTeams() {
		clearTeams()
	}

	fun clearTeams() {
		coloredTeams.values.forEach{
			it.unregister()
		}
		coloredTeams.clear()
	}

	fun addEntityToTeam(id: String, entity: Entity) {
			val machine = configManager.getAllMachines()[id]
			val color = ChatColor.valueOf(machine?.color ?: "GREEN")

			val teamName = "${color}_$teamName"

			if (scoreboard.getTeam(teamName) == null) {
				val newTeam = scoreboard.registerNewTeam(teamName)
				newTeam.prefix = "${color}_$teamName"
				newTeam.color = color
				coloredTeams[id] = newTeam
				add(id, entity)
			} else {
				scoreboard.getTeam(teamName)?.addEntity(entity)
			}

	}


}