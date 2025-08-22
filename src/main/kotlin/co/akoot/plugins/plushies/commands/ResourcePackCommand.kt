package co.akoot.plugins.plushies.commands

import co.akoot.plugins.bluefox.api.FoxCommand
import co.akoot.plugins.bluefox.api.FoxPlugin
import co.akoot.plugins.bluefox.api.Kolor
import co.akoot.plugins.bluefox.extensions.isBedrock
import co.akoot.plugins.bluefox.util.Text
import co.akoot.plugins.plushies.util.ResourcePack.getJavaPack
import co.akoot.plugins.plushies.util.ResourcePack.sendPackMsg
import co.akoot.plugins.plushies.util.ResourcePack.setPack
import org.bukkit.command.CommandSender

class ResourcePackCommand(plugin: FoxPlugin) : FoxCommand(plugin, "resourcepack", aliases = arrayOf("rp", "pack")) {

    override fun onTabComplete(sender: CommandSender, alias: String, args: Array<out String>): MutableList<String> {
        return mutableListOf()
    }

    override fun onCommand(sender: CommandSender, alias: String, args: Array<out String>): Boolean {
        val arg = args.getOrNull(0)

        if (arg == "reload") {
            if (!hasPermission(sender, "reload")) return false
            return if (getJavaPack()) {
                for (p in plugin.server.onlinePlayers.filter { !it.isBedrock }) {
                    Text(p) { Kolor.WARNING("Resource pack has been reloaded") }
                    p.sendPackMsg
                }
                true
            } else {
                Text(sender) { Kolor.ERROR("Could not update link!") }
                false
            }
        }

        val p = playerCheck(sender) ?: return false
        if (p.isBedrock) return false

        return when (arg) {
            "enable", "!" -> setPack(p, true)
            else -> {
                p.sendPackMsg
            }
        }
    }
}