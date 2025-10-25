package me.maxos.different.mineMachines.menu.buttons

import me.maxos.different.mineMachines.model.machines.InstallMachine
import net.j4c0b3y.api.menu.button.Button
import net.j4c0b3y.api.menu.button.ButtonClick
import org.bukkit.inventory.ItemStack

data class CardButton(

	val itemStack: ItemStack,
	val installMachine: InstallMachine,
	val onClickUnit: (ButtonClick) -> Unit,

) : Button() {

	override fun getIcon(): ItemStack {

		return itemStack

	}

	override fun onClick(click: ButtonClick) {
		onClickUnit(click)
	}
}