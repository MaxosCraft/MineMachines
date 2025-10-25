package me.maxos.different.mineMachines.containers

import me.maxos.different.mineMachines.MineMachines
import me.maxos.different.mineMachines.database.DatabaseManager
import me.maxos.different.mineMachines.files.configs.ConfigManager
import me.maxos.different.mineMachines.items.machines.MachineItemStack
import me.maxos.different.mineMachines.model.machines.InstallMachine
import me.maxos.different.mineMachines.model.videocard.VideoCard
import me.maxos.different.mineMachines.utils.logInfo
import me.maxos.different.mineMachines.utils.logWarn
import me.maxos.different.mineMachines.visualizations.LightingMachine
import net.j4c0b3y.api.menu.Menu
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

class MachinesContainer(

	private val machineItemStack: MachineItemStack,
	private val databaseManager: DatabaseManager,
	private val plugin: MineMachines,
	private val configManager: ConfigManager,
	private val lightingMachine: LightingMachine

) {

	private val installedMachinesMap = ConcurrentHashMap<Location, InstallMachine>()


	init {
		initMachinesOnStart()
		deepUpdateMachines()
		saveMapToDB()
	}

	fun reloadAllMachines() {
		deepUpdateMachines()
	}

	private fun deepUpdateMachines() {
		installedMachinesMap.forEach inner@{ (location, installMachine) ->

			installMachine.scheduler?.cancel()
			installMachine.scheduler = null

			val machineModel = configManager.getAllMachines().get(installMachine.typeId)

			if (machineModel == null) {
				location.block.type = Material.AIR
				installedMachinesMap.remove(location)
				databaseManager.delMachineFromDb(location.world.name, location.blockX, location.blockY, location.blockZ)
				return@inner
			}

			installMachine.cashProfits = machineModel.cashProfits
			installMachine.gpuAmount = machineModel.gpuAmount

			val material = machineModel.material
			if (location.block.type != material) location.block.type = material

			databaseManager.updateMachine(
				location.world.name, location.blockX, location.blockY, location.blockZ,
				null, installMachine.cashProfits, installMachine.gpuAmount)

		//	lightingMachine.spawnShulker(location.clone(), installMachine.typeId)

			val currentCards = configManager.getAllGpu()

			val iterator = installMachine.cards?.iterator()
			while (iterator?.hasNext() == true) {
				val card = iterator.next()
				val currentCard = currentCards[card.id]
				if (currentCard == null) {
					iterator.remove()
					if (installMachine.activeGpu > 0) installMachine.activeGpu--
					databaseManager.delAllTypeCard(card.id)
				} else {
					card.profitMargin = currentCard.profitMargin
				}
			}

			if (installMachine.scheduler == null) startMachineUpdater(location)

		}
	}

	fun putMachineInMap(location: Location, installMachine: InstallMachine) {
		installedMachinesMap.put(location, installMachine)
		startMachineUpdater(location)
		lightingMachine.spawnShulker(location, installMachine)
	}

	fun delShulkers() {
		lightingMachine.cleanupAllShulkers(installedMachinesMap.values)
	}

	fun refreshShulkers() {

		delShulkers()

		for ((loc, machine) in installedMachinesMap) {
			if (machine.shulker != null) {
				machine.shulker = null
				lightingMachine.spawnShulker(loc, machine)
			}
		}
	}

	fun getAllShulkers(): List<UUID> {
		val uuidList = mutableListOf<UUID>()
		for (i in installedMachinesMap.values) {
			val uuid = i.shulker
			if (uuid != null) uuidList.add(uuid)
		}
		return uuidList
	}

	private fun initMachinesOnStart() {
		if (databaseManager.checkEmpty()) return
		getAllMachinesFromDB()
	}

	fun pickupCard(world: String, x: Int, y: Int, z: Int, cardId: String) {
		val location = Location(Bukkit.getWorld(world), x.toDouble(), y.toDouble(), z.toDouble())
		val machine = installedMachinesMap[location] ?: return

		val cardToRemove = machine.cards?.find { it.id == cardId }
		cardToRemove?.let { card ->
			machine.cards.remove(card)
			machine.activeGpu -= 1
			val id = databaseManager.getIdMachine(world, x, y, z)!!
			databaseManager.delRandomCardInDb(id, card.id, machine.activeGpu)
			return@let
		}
	}


	private fun getAllMachinesFromDB() {

		val machinesList = databaseManager.loadMachinesFromDb()

		machinesList.forEach { machine ->

			val world = Bukkit.getWorld(machine.world)

			if (world != null) {

				val location = Location(world, machine.x.toDouble(), machine.y.toDouble(), machine.z.toDouble())

				if (location.block.type != Material.AIR) {
					putMachineInMap(location.clone(), machine)
				} else {
					databaseManager.delMachineFromDb(machine.world, machine.x, machine.y, machine.z)
					logWarn("Машина на координатах ${machine.x}, ${machine.y}, ${machine.z} не обнаружена и была удалена!")
				}

			} else {
				databaseManager.delMachineFromDb(machine.world, machine.x, machine.y, machine.z)
				logWarn("Мир ${machine.world} не обнаружен! Удаляем ферму на координатах: ${machine.x}, ${machine.y}, ${machine.z}")
			}

		}

	}

	fun getSumProfits(installMachine: InstallMachine): Double {
		var cashProfitsCard = 0.0
		installMachine.cards?.forEach {
			cashProfitsCard += it.profitMargin
		}
		val sumProfits = installMachine.cashProfits + cashProfitsCard
		return sumProfits
	}

	private fun startMachineUpdater(location: Location) {

		val machine = installedMachinesMap[location] ?: return

		val task = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, Runnable {
			if (machine.scheduler?.isCancelled == false) {

				machine.balance += getSumProfits(machine)
			//	logInfo("§aБаланс машины: ${machine.balance}")

			} else return@Runnable

		}, 20L, 20L)

		machine.scheduler = task
	}


	fun hasLocationMachine(location: Location) = installedMachinesMap.containsKey(location)
	fun getMachine(location: Location) = installedMachinesMap[location]


	fun settingStatusMenu(location: Location, menu: Menu?) {
		installedMachinesMap[location]?.activeMenu = menu
	}

	fun getItemStack(location: Location): ItemStack {
		val id = installedMachinesMap[location]?.typeId
		val itemStack = machineItemStack.getItemStack(id) ?: return machineItemStack.machinesToId.values.first()
		return itemStack
	}

	fun removeMachine(location: Location) {
		installedMachinesMap[location]?.scheduler?.cancel()

		val shulkerUuid = installedMachinesMap[location]?.shulker
		if (shulkerUuid != null) lightingMachine.killShulker(shulkerUuid)

		installedMachinesMap.remove(location)
		databaseManager.delMachineFromDb(location.world.name, location.blockX, location.blockY, location.blockZ)
	}

	fun getMap() = installedMachinesMap.toMap()

	private fun saveMapToDB() {

		Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, Runnable {

			if (!installedMachinesMap.isEmpty()) {

				installedMachinesMap.values.forEach { machine ->

					databaseManager.updateMachine(
						world = machine.world,
						x = machine.x,
						y = machine.y,
						z = machine.z,
						newBalance = machine.balance,
						newCashProfits = machine.cashProfits,
						null
					)
					logInfo("Выполнено сохранение в базу данных!")

				}

			}

		}, 0L, configManager.dbSaveInterval?.toLong() ?: 6000L)

	}

	fun updateMachineCards(location: Location, card: VideoCard) {
		if (installedMachinesMap[location] != null) {
			val amount = installedMachinesMap[location]!!.activeGpu
			installedMachinesMap[location]?.activeGpu = amount + 1
			installedMachinesMap[location]?.cards?.add(card)
		}
	}

	fun resetBalance(location: Location) {
		installedMachinesMap[location]?.balance = 0.0
		databaseManager.updateMachine(location.world.name, location.blockX, location.blockY, location.blockZ, 0.0, null, null)
	}

	fun getBalance(location: Location): Double? {
		return installedMachinesMap[location]?.balance
	}

	fun getOwner(location: Location): Player? {
		return installedMachinesMap[location]?.owner?.let { Bukkit.getPlayer(it) }
	}

}