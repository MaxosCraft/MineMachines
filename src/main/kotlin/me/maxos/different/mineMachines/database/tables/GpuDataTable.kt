package me.maxos.different.mineMachines.database.tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object GpuDataTable : Table("machine_video_cards") {

	val id = integer("id").autoIncrement().primaryKey()

	val machineId = integer("machine_id").references(

		MachineDataTable.id,
		onDelete = ReferenceOption.CASCADE,
		onUpdate = ReferenceOption.CASCADE)

	val cardId = varchar("card_id", 50)
	val slotOrder = integer("slot_order")  // порядок в слотах

}