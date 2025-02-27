package co.akoot.plugins.plushies.commands.bluemap

import co.akoot.plugins.bluefox.api.FoxCommand
import co.akoot.plugins.bluefox.api.FoxPlugin
import co.akoot.plugins.plushies.Plushies.Companion.pluginEnabled
import de.bluecolored.bluemap.api.BlueMapAPI
import org.bukkit.command.CommandSender

class ShowCommand(plugin: FoxPlugin) : FoxCommand(plugin, "show") {

    override fun onTabComplete(sender: CommandSender, alias: String, args: Array<out String>): MutableList<String> {
        if (args.size == 1) {
            return getOnlinePlayerSuggestions(exclude = setOf(sender.name))
        }
        return mutableListOf()
    }

    override fun onCommand(sender: CommandSender, alias: String, args: Array<out String>): Boolean {
        if (!pluginEnabled("BlueMap")) return sendError(sender, "BlueMap is not enabled!")

        val map = BlueMapAPI.getInstance().get()

        val target = if (args.isEmpty()) {
            playerCheck(sender, "Please specify a player") ?: return false
        } else {
            plugin.server.getPlayer(args[0]) ?: return sendError(sender, "Player not found")
        }

        val message = if (target == sender) "You are" else "${target.name} is"

        if (map.webApp.getPlayerVisibility(target.uniqueId)) {
            return sendError(sender, "$message already visible!")
        }

        map.webApp.setPlayerVisibility(target.uniqueId, true)
        return sendMessage(sender,"$message now visible!")
    }
}