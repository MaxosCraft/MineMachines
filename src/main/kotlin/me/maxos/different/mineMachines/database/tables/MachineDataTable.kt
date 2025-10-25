package me.maxos.different.mineMachines.database.tables

import org.jetbrains.exposed.sql.ColumnSet
import org.jetbrains.exposed.sql.Table

object MachineDataTable : Table("MINE_MACHINES") {

	val id = integer("id").autoIncrement().primaryKey() // Уникальный ID каждой машины
	val playerUUID = uuid("player_uuid") // UUID игрока-владельца
	val type = varchar("type_id", 50) // Тип машины (без uniqueIndex!)
	val balance = double("balance").default(0.0) // балик
	val cashProfits = double("cash_profits").default(0.0) // прибыль

	val gpuAmount = integer("gpu_amount")
	val activeGpu = integer("active_gpu")

	val world = varchar("world", 50) // Мир для локи
	val x = integer("loc_x") // Корды локи хуz
	val y = integer("loc_y")
	val z = integer("loc_z")

}