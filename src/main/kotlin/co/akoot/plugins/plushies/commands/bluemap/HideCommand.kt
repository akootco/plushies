package co.akoot.plugins.plushies.commands.bluemap

import co.akoot.plugins.bluefox.api.FoxCommand
import co.akoot.plugins.bluefox.api.FoxPlugin
import co.akoot.plugins.plushies.Plushies.Companion.pluginEnabled
import de.bluecolored.bluemap.api.BlueMapAPI
import org.bukkit.command.CommandSender

class HideCommand(plugin: FoxPlugin) : FoxCommand(plugin, "hide") {

    override fun onTabComplete(sender: CommandSender, alias: String, args: Array<out String>): MutableList<String> {
        return mutableListOf()
    }

    override fun onCommand(sender: CommandSender, alias: String, args: Array<out String>): Boolean {
        if (!pluginEnabled("BlueMap")) return sendError(sender, "BlueMap is not enabled!")

        val map = BlueMapAPI.getInstance().get()

        val p = playerCheck(sender) ?: return false

        if (!map.webApp.getPlayerVisibility(p.uniqueId)) {
            return sendError(sender, "You are already hidden!")
        }

        map.webApp.setPlayerVisibility(p.uniqueId, false)
        return sendMessage(sender, "You are now hidden!")
    }
}