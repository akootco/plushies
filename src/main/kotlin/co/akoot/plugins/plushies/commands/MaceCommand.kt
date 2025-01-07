package co.akoot.plugins.plushies.commands

import co.akoot.plugins.bluefox.api.FoxCommand
import co.akoot.plugins.bluefox.api.FoxPlugin
import org.bukkit.Material
import org.bukkit.command.CommandSender

class MaceCommand(plugin: FoxPlugin) : FoxCommand(plugin, "mace") {

    override fun onTabComplete(sender: CommandSender, args: Array<out String>): MutableList<String> {
        return mutableListOf()
    }

    override fun onCommand(sender: CommandSender, alias: String, args: Array<out String>): Boolean {
        val p = playerCheck(sender) ?: return false

        val item = p.inventory.itemInMainHand
        val meta = item.itemMeta

        if (item.type != Material.MACE || meta == null) {
            sendError(p, "You need to be holding a mace")
            return false
        }

        meta.setCustomModelData(999)
        item.setItemMeta(meta)
        return true
    }
}