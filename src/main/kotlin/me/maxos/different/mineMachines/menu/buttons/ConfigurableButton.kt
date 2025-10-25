package me.maxos.different.mineMachines.menu.buttons

import net.j4c0b3y.api.menu.button.ButtonClick
import net.j4c0b3y.api.menu.button.Button
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType


data class ConfigurableButton(
	val material: Material,
	val name: String,
	val setLore: List<String>,
	val onClickUnit: (ButtonClick) -> Unit,
	val requiredType: RequiredButton?,
	val namespacedKey: NamespacedKey?,

	private val placeholders: Map<String, String>? = emptyMap()
) : Button() {

	override fun getIcon(): ItemStack {
		val itemStack = ItemStack(this.material, 1)
		val itemMeta = itemStack.itemMeta ?: return itemStack

		if (placeholders != null) {
			val displayName = replacePlaceholders(this.name, placeholders)
			itemMeta.setDisplayName(displayName)
			val loreWithPlaceholders = setLore.map { line ->
				replacePlaceholders(line, placeholders)
			}
			itemMeta.lore = loreWithPlaceholders
		} else {
			itemMeta.setDisplayName(this.name)
			itemMeta.lore = setLore
		}


		if (requiredType != null && namespacedKey != null) {
			itemMeta.persistentDataContainer.set(
				namespacedKey,
				PersistentDataType.STRING,
				requiredType.pdcKey
			)
		}

		itemStack.itemMeta = itemMeta
		return itemStack
	}

	override fun onClick(click: ButtonClick) {
		onClickUnit(click)
	}

	private fun replacePlaceholders(text: String, placeholders: Map<String, String>): String {
		var result = text
		placeholders.forEach { (placeholder, replacement) ->
			result = result.replace(placeholder, replacement)
		}
		return result
	}
}
