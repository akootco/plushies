package co.akoot.plugins.plushies.commands
import co.akoot.plugins.bluefox.api.FoxCommand
import co.akoot.plugins.bluefox.api.FoxPlugin
import co.akoot.plugins.plushies.gui.SellIHeadMenu
import org.bukkit.command.CommandSender

class SellHeadsCommand(plugin: FoxPlugin) : FoxCommand(plugin, "sellheads", "sell heads") {

    override fun onTabComplete(sender: CommandSender, alias: String, args: Array<out String>): MutableList<String> {
        return mutableListOf()
    }

    override fun onCommand(sender: CommandSender, alias: String, args: Array<out String>): Boolean {
        val p = playerCheck(sender) ?: return false
        p.openInventory(SellIHeadMenu().inventory)
        return true
    }
}