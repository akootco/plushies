package co.akoot.plugins.plushies.commands
import co.akoot.plugins.bluefox.api.FoxCommand
import co.akoot.plugins.bluefox.api.FoxPlugin
import co.akoot.plugins.plushies.gui.SellItemMenu
import org.bukkit.command.CommandSender

class SellItemsCommand(plugin: FoxPlugin) : FoxCommand(plugin, "sellitems", "sellitems") {

    override fun onTabComplete(sender: CommandSender, alias: String, args: Array<out String>): MutableList<String> {
        return mutableListOf()
    }

    override fun onCommand(sender: CommandSender, alias: String, args: Array<out String>): Boolean {
        val p = playerCheck(sender) ?: return false
        p.openInventory(SellItemMenu().inventory)
        return true
    }
}