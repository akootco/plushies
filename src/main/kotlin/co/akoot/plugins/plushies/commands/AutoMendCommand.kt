package co.akoot.plugins.plushies.commands

import co.akoot.plugins.bluefox.api.CatCommand
import co.akoot.plugins.bluefox.util.Text.Companion.enabled
import co.akoot.plugins.bluefox.util.primary
import co.akoot.plugins.bluefox.util.sendText
import co.akoot.plugins.plushies.Plushies
import co.akoot.plugins.plushies.util.Util.autoMend

class AutoMendCommand(plugin: Plushies) : CatCommand(plugin, "automend") {
    init {
        noargs {
            val player = getPlayerSender(it) ?: return@noargs false
            player.sendText("AutoMend: ", primary(player.autoMend.enabled))
        }
        then {
            boolean {
                val player = getPlayerSender(it) ?: return@boolean false
                player.autoMend = getBoolean(it)
                player.sendText("AutoMend is now ", primary(player.autoMend.enabled))
                true
            }
        }
    }
}