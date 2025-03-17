package co.akoot.plugins.plushies.commands

import co.akoot.plugins.bluefox.api.FoxCommand
import co.akoot.plugins.bluefox.api.FoxPlugin
import co.akoot.plugins.plushies.gui.BookArchiveMenu
import co.akoot.plugins.plushies.gui.BookMenu
import org.bukkit.command.CommandSender

class BookCommand(plugin: FoxPlugin) : FoxCommand(plugin, "book") {

    override fun onTabComplete(sender: CommandSender, alias: String, args: Array<out String>): MutableList<String> {
        return mutableListOf()
    }

    override fun onCommand(sender: CommandSender, alias: String, args: Array<out String>): Boolean {
        val p = playerCheck(sender)?: return false

        when (args.getOrNull(0)) {
            "archive" -> {
                p.openInventory(BookArchiveMenu(plugin).inventory)
                return true
            }

            else -> {
                p.openInventory(BookMenu().inventory)
                return true
            }
        }
    }
}
