package co.akoot.plugins.plushies.commands

import co.akoot.plugins.bluefox.api.FoxCommand
import co.akoot.plugins.bluefox.api.FoxPlugin
import co.akoot.plugins.plushies.Plushies.Companion.plushieConf
import co.akoot.plugins.plushies.gui.PlushieMenu
import co.akoot.plugins.plushies.items.PlushieItems
import co.akoot.plugins.plushies.items.PlushieItems.plushies
import co.akoot.plugins.plushies.util.Util.plushMsg
import org.bukkit.Material
import org.bukkit.command.CommandSender

class PlushieCommand(plugin: FoxPlugin) : FoxCommand(plugin, "plushie", aliases = arrayOf("plush")) {

    override fun onTabComplete(sender: CommandSender, alias: String, args: Array<out String>): MutableList<String> {

        if (args.size == 1) {
            return plushies.map { it.id }.toMutableList()
        } else if (args.size == 2 && plushies.any { it.id.equals(args[0], ignoreCase = true) }) {
            return arrayListOf("statue")
        }

        return mutableListOf()
    }

    override fun onCommand(sender: CommandSender, alias: String, args: Array<out String>): Boolean {
        val p = playerCheck(sender) ?: return false

        if (args.isEmpty()) {
            p.openInventory(PlushieMenu().inventory)
            return true
        }

        val arg = args[0]

        if (arg in setOf("reload", "load") && hasPermission(sender, "reload")) {
            plushies = PlushieItems.load(plushieConf)
            return sendMessage(sender, "Plushies reloaded!")
        }

        val plushie = plushies.find { it.id.equals(arg, ignoreCase = true) }
            ?: return sendError(sender, "$arg does not exist!")

        if (p.inventory.itemInMainHand.type != Material.TOTEM_OF_UNDYING) {
            return sendError(sender, "You must be holding a totem!")
        }

        val cmd = if (args.getOrNull(1) == "statue") {
            plushie.model.toIntOrNull()?.plus(1)?.toString()
                ?: "${plushie.model}.st"
        } else {
            plushie.model
        }

        p.inventory.setItemInMainHand(plushie.item(cmd))
        p.sendMessage(plushMsg(plushie.name).component)

        return true
    }
}
