package me.maxos.different.mineMachines.menu.template

import me.maxos.different.mineMachines.MineMachines
import me.maxos.different.mineMachines.files.configs.menu.MenuConfigManager
import me.maxos.different.mineMachines.menu.buttons.ConfigurableButton
import me.maxos.different.mineMachines.menu.buttons.RequiredButton
import me.maxos.different.mineMachines.menu.buttons.RequiredButton.Companion.fromCardMenu
import me.maxos.different.mineMachines.menu.buttons.RequiredButton.Companion.fromMainMenu
import me.maxos.different.mineMachines.menu.buttons.action.ClickButton
import me.maxos.different.mineMachines.model.machines.InstallMachine
import net.j4c0b3y.api.menu.Menu
import net.j4c0b3y.api.menu.layer.impl.BackgroundLayer
import net.j4c0b3y.api.menu.layer.impl.ForegroundLayer
import net.j4c0b3y.api.menu.pagination.PaginatedMenu
import net.j4c0b3y.api.menu.template.Template
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class CardTemplate(

	private val menu: Menu,
	private val menuConfigManager: MenuConfigManager,
	private val clickButton: ClickButton,
	private val plugin: MineMachines,
	private val installMachine: InstallMachine,
	private val items: List<ItemStack>,
	private val paginatedMenu: PaginatedMenu,
	private val currentItems: MutableList<ItemStack>

	) : Template {

	override fun apply(background: BackgroundLayer, foreground: ForegroundLayer) {

		for (i in menuConfigManager.decorNewButtonsCard) {

			for (j in i.slots) {
				foreground[j] = ConfigurableButton(
					i.material,
					i.name,
					i.lore,
					onClickUnit = {},
					null,
					null,
					null
				)
			}

		}

		menuConfigManager.mapRequiredButtons.forEach {
			val id = it.key
			val type = RequiredButton.fromId(id)
			if (!fromCardMenu(type)) return@forEach
			val button = it.value

			foreground[button.slots.first()] = ConfigurableButton(
				button.material,
				button.name,
				button.lore,
				onClickUnit = { click ->
					clickButton.clickAction(
						type,
						click,
						installMachine,
						items,
						menu,
						paginatedMenu,
						clickButton,
						currentItems
					)
				},
				type,
				plugin.menuIconRequiredKey,
				null
			)
		}



	}

}