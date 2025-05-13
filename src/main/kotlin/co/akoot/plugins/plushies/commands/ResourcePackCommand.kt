package co.akoot.plugins.plushies.commands

import co.akoot.plugins.bluefox.api.FoxCommand
import co.akoot.plugins.bluefox.api.FoxPlugin
import co.akoot.plugins.bluefox.extensions.isBedrock
import co.akoot.plugins.plushies.util.ResourcePack.sendPackLink
import co.akoot.plugins.plushies.util.ResourcePack.setPack
import org.bukkit.command.CommandSender

class ResourcePackCommand(plugin: FoxPlugin) : FoxCommand(plugin, "resourcepack", aliases = arrayOf("rp", "pack")) {

    override fun onTabComplete(sender: CommandSender, alias: String, args: Array<out String>): MutableList<String> {
        return mutableListOf()
    }

    override fun onCommand(sender: CommandSender, alias: String, args: Array<out String>): Boolean {
        val p = playerCheck(sender) ?: return false
        if (p.isBedrock) return false

        return when (args.getOrNull(0)) {
            "enable", "!" -> setPack(p, true)
            else -> { p.sendPackLink }
        }
    }
}