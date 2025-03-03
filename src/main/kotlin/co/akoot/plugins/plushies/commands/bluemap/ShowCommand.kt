package co.akoot.plugins.plushies.commands.bluemap

import co.akoot.plugins.bluefox.api.FoxCommand
import co.akoot.plugins.bluefox.api.FoxPlugin
import co.akoot.plugins.plushies.Plushies.Companion.pluginEnabled
import de.bluecolored.bluemap.api.BlueMapAPI
import org.bukkit.command.CommandSender

class ShowCommand(plugin: FoxPlugin) : FoxCommand(plugin, "show") {

    override fun onTabComplete(sender: CommandSender, alias: String, args: Array<out String>): MutableList<String> {
        return mutableListOf()
    }

    override fun onCommand(sender: CommandSender, alias: String, args: Array<out String>): Boolean {
        if (!pluginEnabled("BlueMap")) return sendError(sender, "BlueMap is not enabled!")

        val map = BlueMapAPI.getInstance().get()

        val p = playerCheck(sender) ?: return false

        if (map.webApp.getPlayerVisibility(p.uniqueId)) {
            return sendError(sender, "You are already visible!")
        }

        map.webApp.setPlayerVisibility(p.uniqueId, true)
        return sendMessage(sender,"You are now visible!")
    }
}