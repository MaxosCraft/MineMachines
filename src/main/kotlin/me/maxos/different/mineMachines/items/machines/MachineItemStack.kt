package me.maxos.different.mineMachines.items.machines

import me.maxos.different.mineMachines.MineMachines
import me.maxos.different.mineMachines.files.configs.ConfigManager
import me.maxos.different.mineMachines.utils.logInfo
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import java.util.concurrent.ConcurrentHashMap
import org.bukkit.persistence.PersistentDataType


class MachineItemStack(

	private val configManager: ConfigManager,
	private val plugin: MineMachines

) {

	val machinesToId = ConcurrentHashMap<String, ItemStack>()

	init {
		createItemsMachines()
		/*
		machinesToId.forEach {
			logInfo("Машина ${it.key} создана: ${it.value}")
		}
		 */
	}

	fun reloadMachinesItems() {
		machinesToId.clear()
		createItemsMachines()
	}

	private fun createItemsMachines() {

		val machines = configManager.getAllMachines().values

		machines.forEach {

			val item = ItemStack(it.material)
			val itemMeta = item.itemMeta

			itemMeta.setDisplayName(it.name)
			itemMeta.lore = it.lore

			if (it.glow) {
				itemMeta.addEnchant(Enchantment.LUCK, 1, true)
				itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
			}

			val pdc = itemMeta.persistentDataContainer
			pdc.set(plugin.machinesKey, PersistentDataType.STRING, it.id)
			pdc.set (plugin.cashProfitsKey, PersistentDataType.DOUBLE, it.cashProfits)
			pdc.set(plugin.gpuAmount, PersistentDataType.INTEGER, it.gpuAmount)

			item.itemMeta = itemMeta

			machinesToId[it.id] = item

		}

	}

	fun getItemStack(id: String?) = machinesToId[id]

}