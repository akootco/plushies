package co.akoot.plugins.plushies.commands

import co.akoot.plugins.bluefox.api.FoxCommand
import co.akoot.plugins.bluefox.api.FoxPlugin
import co.akoot.plugins.bluefox.api.Kolor
import co.akoot.plugins.bluefox.extensions.hasPDC
import co.akoot.plugins.bluefox.extensions.removePDC
import co.akoot.plugins.bluefox.extensions.setPDC
import co.akoot.plugins.bluefox.util.Text
import co.akoot.plugins.bluefox.util.Text.Companion.now
import co.akoot.plugins.plushies.Plushies.Companion.key
import org.bukkit.command.CommandSender

class IHateJumpBoostCommand(plugin: FoxPlugin) : FoxCommand(plugin, "antijump") {

    override fun onTabComplete(sender: CommandSender, alias: String, args: Array<out String>): MutableList<String> {
        return mutableListOf()
    }

    override fun onCommand(sender: CommandSender, alias: String, args: Array<out String>): Boolean {
        val player = playerCheck(sender) ?: return false
        val key = key("plsnojump")

        player.apply {
            if (player.hasPDC(key)) removePDC(key)
            else setPDC(key, true)
            Text(this) {
                Kolor.QUOTE("You are ${(player.hasPDC(key).not()).now} affected by jump boost beacons")
            }
        }

        return true
    }
}
