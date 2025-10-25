package me.maxos.different.mineMachines.utils

import java.util.logging.Logger

// так надо
private val log = Logger.getLogger("MineMachines")

fun logInfo (msg: String) { // зачилься

	log.info(msg)

}

fun logWarn (msg: String) { // напрягись

	log.warning(msg)

}

fun logError (msg: String) { // умри

	log.severe(msg)

}