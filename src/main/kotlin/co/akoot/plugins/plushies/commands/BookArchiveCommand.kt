package co.akoot.plugins.plushies.commands

import co.akoot.plugins.bluefox.api.FoxCommand
import co.akoot.plugins.bluefox.api.FoxPlugin
import co.akoot.plugins.plushies.util.BookArchiver
import org.bukkit.command.CommandSender

class BookArchiveCommand(plugin: FoxPlugin) : FoxCommand(plugin, "savebook", aliases = arrayOf("sb", "archive")) {

    override fun onTabComplete(sender: CommandSender, alias: String, args: Array<out String>): MutableList<String> {
        return mutableListOf()
    }

    override fun onCommand(sender: CommandSender, alias: String, args: Array<out String>): Boolean {
        val p = playerCheck(sender) ?: return false

        BookArchiver(plugin).saveBook(p, p.inventory.itemInMainHand)
        return true
    }
}