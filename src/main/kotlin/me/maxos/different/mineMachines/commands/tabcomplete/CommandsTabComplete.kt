package me.maxos.different.mineMachines.commands.tabcomplete

import me.maxos.different.mineMachines.items.machines.MachineItemStack
import me.maxos.different.mineMachines.items.videocards.GpuItemStack
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

class CommandsTabComplete(

	private val machineItemStack: MachineItemStack,
	private val gpuItemStack: GpuItemStack

): TabCompleter {

	private var machinesIdList: MutableList<String>? = null
	private var gpuIdList: MutableList<String>? = null

	init {
		fillingList()
	}

	fun fillingList() {
		machinesIdList = machineItemStack.machinesToId.keys.toMutableList()
		gpuIdList = gpuItemStack.gpuContainer.keys.toMutableList()
	}

	override fun onTabComplete(
		sender: CommandSender,
		cmd: Command,
		string: String,
		args: Array<out String>?
	): MutableList<String>? {

		if (args?.size == 1) {
			return mutableListOf("give-machine", "give-card", "reload").filter {
				it.startsWith(args[0], ignoreCase = true)
			}.toMutableList()
		}

		if (args?.size == 3) {
			when (args[0]) {
				"give-machine" -> return machinesIdList
				"give-card" -> return gpuIdList
			}
		}

		return null

	}
}