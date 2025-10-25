package me.maxos.different.mineMachines.events.machine

import me.maxos.different.mineMachines.MineMachines
import me.maxos.different.mineMachines.containers.MachinesContainer
import me.maxos.different.mineMachines.database.tables.MachineDataTable
import me.maxos.different.mineMachines.files.configs.MessagesManager
import me.maxos.different.mineMachines.items.machines.MachineItemStack
import me.maxos.different.mineMachines.model.machines.InstallMachine
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.persistence.PersistentDataType.*
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction

class BlockPlace(

	private val machineItemStack: MachineItemStack,
	private val plugin: MineMachines,
	private val machinesContainer: MachinesContainer,
	private val messagesManager: MessagesManager

): Listener {

	@EventHandler(ignoreCancelled = true)
	fun machinePlace(e: BlockPlaceEvent) {

		val player = e.player
		val block = e.blockPlaced
		val item = e.itemInHand
		val location = block.location.clone()

		if (!item.itemMeta.persistentDataContainer.has(plugin.machinesKey)) return

		machineItemStack.machinesToId.forEach { entry ->

			val itemStack = entry.value
			val pdcKey = plugin.machinesKey

			val handMachineId = e.itemInHand.itemMeta.persistentDataContainer.get(pdcKey, STRING)
			val configMachineId = itemStack.itemMeta.persistentDataContainer.get(pdcKey, STRING)

			if (handMachineId == configMachineId) {

				val pdc = itemStack.itemMeta.persistentDataContainer
				val cash = pdc.get(plugin.cashProfitsKey, DOUBLE) ?: 1000.0
				val gpuAmount = pdc.get(plugin.gpuAmount, INTEGER) ?: 1

				transaction {

					MachineDataTable.insert {
						it[playerUUID] = player.uniqueId
						it[type] = entry.key
						// баланс
						it[balance] = 0.0
						// прибыль
						it[cashProfits] = cash
						it[this.gpuAmount] = gpuAmount
						it[activeGpu] = 0
						// локация
						it[world] = block.world.name
						it[x] = block.x
						it[y] = block.y
						it[z] = block.z

					}

				}

				machinesContainer.putMachineInMap(location,

					InstallMachine(
						entry.key,
						0.0,
						cash,
						gpuAmount,
						0,
						player.uniqueId,
						null,

						world = block.world.name,
						x = block.x,
						y = block.y,
						z = block.z,
						cards = mutableListOf(),
						null,
						null
					)

				)

				player.sendMessage(messagesManager.getMessage("place", "§aМайнинг-ферма установлена!"))

				if (block.type != itemStack.type) block.type = itemStack.type

			}

		}

	}

}