package me.maxos.different.mineMachines.commands

import me.maxos.different.mineMachines.MineMachines
import me.maxos.different.mineMachines.containers.MachinesContainer
import me.maxos.different.mineMachines.database.tables.GpuDataTable
import me.maxos.different.mineMachines.database.tables.MachineDataTable
import me.maxos.different.mineMachines.files.configs.MessagesManager
import me.maxos.different.mineMachines.items.machines.MachineItemStack
import me.maxos.different.mineMachines.items.videocards.GpuItemStack
import me.maxos.different.mineMachines.utils.logInfo
import me.maxos.different.mineMachines.visualizations.LightingMachine
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class PluginCommands(

	private val machineItemStack: MachineItemStack,
	private val gpuItemStack: GpuItemStack,
	private val plugin: MineMachines,
	private val messagesManager: MessagesManager

): CommandExecutor {

	override fun onCommand(sender: CommandSender, cmd: Command, string: String, args: Array<out String>?): Boolean {

		if (args == null || args.isEmpty()) return false

		if (args[0] == "reload") {
			plugin.onReload()
			sender.sendMessage(messagesManager.getMessage("reload", "§aПлагин перезагружен!"))
			return true
		}

		if (args[0] == "give-machine" || args[0] == "give-card") {

			if (args[1].isEmpty() || args[2].isEmpty()) return false

			val targetPlayer = Bukkit.getPlayer(args[1])

			if (targetPlayer == null) {
				sender.sendMessage(messagesManager.getMessage("non-online", "§cИгрок не в сети!"))
				return true
			}

			if (targetPlayer.inventory.firstEmpty() == -1) {
				sender.sendMessage(messagesManager.getMessage("non-slots", "§cУ игрока нету свободных слотов в инвентаре!"))
				return true
			}

			when (args[0]) {
				"give-machine" -> {
					machineItemStack.machinesToId[args[2]]?.let {
						targetPlayer.inventory.addItem(it)
						targetPlayer.sendMessage(messagesManager
							.getMessage("get-machine", "§aВам выдали майнинг-ферму {name}!")
							.replace("{name}", it.itemMeta.displayName)
						)
						return true
					}
					sender.sendMessage(messagesManager.getMessage("incorrect-machine", "§cНеверный айди майнинг-фермы!"))
					return true
				}
				"give-card" -> {
					gpuItemStack.gpuContainer[args[2]]?.let {
						targetPlayer.inventory.addItem(it)
						targetPlayer.sendMessage(messagesManager
							.getMessage("get-gpu", "§aВам выдали видеокарту {name}!")
							.replace("{name}", it.itemMeta.displayName)
						)
						return true
					}
					sender.sendMessage(messagesManager.getMessage("incorrect-gpu", "§cНеверный айди видеокарты!"))
					return true
				}
			}

		}

		return false

		/*
		if (args[0] == "debug") {

			logInfo("\n" + "§aМАПА УСТАНОВЛЕННЫХ МАШИН:")
			logInfo(machinesContainer.getMap().toString() + "\n")

			logInfo("§aПРЕДМЕТЫ:")
			logInfo(machineItemStack.machinesToId.toString() + "\n")

		}

		if (args[0] == "debug_db") {
			transaction {
				MachineDataTable.selectAll().forEach { row ->

					logInfo("§aБДШКА МАШИН")
					logInfo("§e$row")

				}

				GpuDataTable.selectAll().forEach { row ->

					logInfo("§aБДШКА ВИДЕОКАРТ")
					logInfo("§e$row")

				}
			}

		}

 */

	}


}