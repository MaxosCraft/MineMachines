package me.maxos.different.mineMachines.items.videocards

import me.maxos.different.mineMachines.MineMachines
import me.maxos.different.mineMachines.files.configs.ConfigManager
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import java.util.concurrent.ConcurrentHashMap

class GpuItemStack(

	private val configManager: ConfigManager,
	private val plugin: MineMachines

) {

	val gpuContainer = ConcurrentHashMap<String, ItemStack>()

	init {
		createItemsGpu()
		/*
		gpuContainer.forEach {
			logInfo("Видеокарта ${it.key} создана: ${it.value}")
		}
		 */
	}

	fun reloadGpuItems() {
		gpuContainer.clear()
		createItemsGpu()
	}

	private fun createItemsGpu() {

		val videoCards = configManager.getAllGpu().values

		videoCards.forEach {

			val item = ItemStack(it.material)
			val itemMeta = item.itemMeta

			itemMeta.setDisplayName(it.name)
			itemMeta.lore = it.lore

			if (it.glow) {
				itemMeta.addEnchant(Enchantment.LUCK, 1, true)
				itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
			}

			val pdc = itemMeta.persistentDataContainer
			pdc.set(plugin.gpuKey, PersistentDataType.STRING, it.id)
			pdc.set (plugin.gpuProfitMargin, PersistentDataType.DOUBLE, it.profitMargin)

			item.itemMeta = itemMeta

			gpuContainer[it.id] = item

		}

	}

	fun getItemStack(id: String?) = gpuContainer[id]

}