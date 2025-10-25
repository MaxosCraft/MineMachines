package me.maxos.different.mineMachines.database

import me.maxos.different.mineMachines.MineMachines
import me.maxos.different.mineMachines.database.tables.GpuDataTable
import me.maxos.different.mineMachines.database.tables.MachineDataTable
import me.maxos.different.mineMachines.database.tables.MachineDataTable.balance
import me.maxos.different.mineMachines.database.tables.MachineDataTable.cashProfits
import me.maxos.different.mineMachines.files.configs.ConfigManager
import me.maxos.different.mineMachines.model.machines.InstallMachine
import me.maxos.different.mineMachines.model.videocard.VideoCard
import me.maxos.different.mineMachines.utils.logError
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File

class DatabaseManager(

	private val plugin: MineMachines,
	private val configManager: ConfigManager

) {

	init {
		connect()
	}

	private fun connect() {
		val databaseFile = File(plugin.dataFolder, "database")
		if (!databaseFile.exists()) {
			databaseFile.mkdirs()
		}

		val dbUrl = "jdbc:h2:file:${databaseFile.absolutePath}/machines;DB_CLOSE_DELAY=-1"

		Database.connect(
			url = dbUrl,
			driver = "org.h2.Driver"
		)

		// таблицы
		transaction {
			SchemaUtils.create(MachineDataTable)
			SchemaUtils.create(GpuDataTable)
		}

		plugin.logger.info("База данных H2 подключена успешно!")
	}

	fun delMachineFromDb(world: String, x: Int, y: Int, z: Int) {

		transaction {
			MachineDataTable.deleteWhere {
				(MachineDataTable.world eq world) and
						(MachineDataTable.x eq x) and
						(MachineDataTable.y eq y) and
						(MachineDataTable.z eq z)
			}
		}

	}

	fun checkEmpty(): Boolean {
		var boolean = true
		transaction {
			boolean = MachineDataTable.selectAll().empty()
		}
		return boolean
	}

	fun saveGpuInMachine(world: String, x: Int, y: Int, z: Int, cardId: String, slot: Int) {

		transaction {
			val id = getIdMachine(world, x, y, z) ?: return@transaction
			GpuDataTable.insert {

				it[machineId] = id
				it[this.cardId] = cardId
				it[slotOrder] = slot

			}
			MachineDataTable.update(
				{
					(MachineDataTable.id eq id)
				}
			) {
				it[activeGpu] = slot
			}
		}
	}

	fun getIdMachine(world: String, x: Int, y: Int, z: Int): Int? {

		return transaction {
			MachineDataTable.select {
				(MachineDataTable.world eq world) and
						(MachineDataTable.x eq x) and
						(MachineDataTable.y eq y) and
						(MachineDataTable.z eq z)
			}.map {
				it[MachineDataTable.id]
			}.firstOrNull()
		}

	}

	fun updateMachine(world: String, x: Int, y: Int, z: Int,
					  newBalance: Double?, newCashProfits: Double?,
					  newGpuAmount: Int?) {

		transaction {
			MachineDataTable.update(
				{
					(MachineDataTable.world eq world) and
							(MachineDataTable.x eq x) and
							(MachineDataTable.y eq y) and
							(MachineDataTable.z eq z)
				}
			) {
				if (newBalance != null) it[balance] = newBalance
				if (newCashProfits != null) it[cashProfits] = newCashProfits
				if (newGpuAmount != null) it[gpuAmount] = newGpuAmount
			}
		}
	}

	fun delRandomCardInDb(machineId: Int, cardId: String, cardAmount: Int): Boolean {
		return transaction {
			val record = GpuDataTable.select {
				(GpuDataTable.machineId eq machineId) and (GpuDataTable.cardId eq cardId)
			}.limit(1)
				.firstOrNull()

			record?.let {
				GpuDataTable.deleteWhere {
					GpuDataTable.id eq record[GpuDataTable.id]
				}
				MachineDataTable.update(
					{
						(MachineDataTable.id eq machineId)
					}
				) {
					it[activeGpu] = cardAmount
				}
				true

			} ?: false

		}
	}

	fun delAllTypeCard(cardId: String) {
		transaction {
			GpuDataTable.deleteWhere {
				GpuDataTable.cardId eq cardId
			}
		}
	}


	fun loadMachinesFromDb(): List<InstallMachine> {

		val machinesList = mutableListOf<InstallMachine>()

		try {
			transaction {

				MachineDataTable.selectAll().forEach { row ->

					val installMachine = InstallMachine(
						row[MachineDataTable.type],
						row[balance],
						row[cashProfits],
						row[MachineDataTable.gpuAmount],
						row[MachineDataTable.activeGpu],
						row[MachineDataTable.playerUUID],
						scheduler = null,
						row[MachineDataTable.world],
						row[MachineDataTable.x],
						row[MachineDataTable.y],
						row[MachineDataTable.z],
						getMachineVideoCards(row[MachineDataTable.id]),
						null,
						null
					)

					machinesList.add(installMachine)

				}

			}

		} catch (e: Exception) {
			logError("Ошибка при подгрузке ферм из БД!")
		}

		return machinesList

	}

	private fun getMachineVideoCards(machineId: Int): MutableList<VideoCard> {

		val cardsList = mutableListOf<VideoCard>()

		val cardsId = transaction {
			GpuDataTable
				.select { GpuDataTable.machineId eq machineId }
				.orderBy(GpuDataTable.slotOrder)
				.map { it[GpuDataTable.cardId] }
		}

		cardsId.forEach {
			if (configManager.getGpu(it) != null) {
				cardsList.add(configManager.getGpu(it)!!)
			}
		}

		return cardsList

	}

}