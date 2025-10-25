package me.maxos.different.mineMachines.menu.template

import me.maxos.different.mineMachines.MineMachines
import me.maxos.different.mineMachines.containers.MachinesContainer
import me.maxos.different.mineMachines.files.configs.menu.MenuConfigManager
import me.maxos.different.mineMachines.model.machines.InstallMachine
import me.maxos.different.mineMachines.menu.buttons.ConfigurableButton
import me.maxos.different.mineMachines.menu.buttons.RequiredButton
import me.maxos.different.mineMachines.menu.buttons.RequiredButton.Companion.fromMainMenu
import net.j4c0b3y.api.menu.*
import net.j4c0b3y.api.menu.layer.impl.BackgroundLayer
import net.j4c0b3y.api.menu.layer.impl.ForegroundLayer
import net.j4c0b3y.api.menu.template.Template
import org.bukkit.Bukkit
import me.maxos.different.mineMachines.menu.buttons.action.ClickButton
import net.j4c0b3y.api.menu.pagination.PaginatedMenu
import org.bukkit.inventory.ItemStack
import kotlin.reflect.jvm.internal.impl.descriptors.Visibilities.Private


class MenuTemplate(
	private val menu: Menu,
	private val menuConfigManager: MenuConfigManager,
	private val clickButton: ClickButton,
	private val plugin: MineMachines,
	private val installMachine: InstallMachine?,
	private val items: List<ItemStack>,
	private val machinesContainer: MachinesContainer

	) : Template {
	override fun apply(background: BackgroundLayer, foreground: ForegroundLayer) {

		val placeholders = preparePlaceholders()

		for (i in menuConfigManager.decorNewButtonsMain) {
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

		if (installMachine != null) {

			menuConfigManager.mapRequiredButtons.forEach {
				val id = it.key
				val type = RequiredButton.fromId(id)
				if (!fromMainMenu(type)) return@forEach
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
							null,
							null,
							null
						)
					},
					type,
					plugin.menuIconRequiredKey,
					placeholders
				)
			}
		}
	}

	private fun preparePlaceholders(): Map<String, String> {
		return mutableMapOf<String, String>().apply {
			if (installMachine != null) {
				put("{owner}", Bukkit.getPlayer(installMachine.owner)?.name ?: "Отсутствует")
				put("{profits}", machinesContainer.getSumProfits(installMachine).toString())
				put("{balance}", installMachine.balance.toString())
				put("{amount-gpu}", installMachine.activeGpu.toString())
				put("{max-gpu}", installMachine.gpuAmount.toString())
			} else {
				put("{owner}", "Неизвестно")
				put("{profits}", "0")
				put("{balance}", "0")
				put("{amount-gpu}", "0")
				put("{max-gpu}", "0")
			}
		}
	}
}
