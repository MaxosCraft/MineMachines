package me.maxos.different.mineMachines.menu.buttons

enum class RequiredButton(

	val id: String,
	val pdcKey: String,
	val menu: String
) {

	INFO("info", "info_icon", "main"),

	BALANCE("balance", "balance_icon", "main"),

	VIDEOCARDS("videocards", "videocards_icon", "main"),

	NEXT("next", "next_icon", "card"),

	BACK("back", "back_icon", "card"),

	HOME("home", "home_icon", "card");

	companion object {
		fun fromId(id: String): RequiredButton? {
			return values().find { it.id == id }
		}
		fun fromMainMenu(requiredButton: RequiredButton?): Boolean {
			return requiredButton?.menu == "main"
		}
		fun fromCardMenu(requiredButton: RequiredButton?): Boolean {
			return requiredButton?.menu == "card"
		}
	}

}

