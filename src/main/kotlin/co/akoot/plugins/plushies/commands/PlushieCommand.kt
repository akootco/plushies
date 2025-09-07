package co.akoot.plugins.plushies.commands

import co.akoot.plugins.bluefox.api.FoxCommand
import co.akoot.plugins.bluefox.api.FoxPlugin
import co.akoot.plugins.plushies.Plushies.Companion.plushieConf
import co.akoot.plugins.plushies.gui.PlushieMenu
import co.akoot.plugins.plushies.util.Items.createPlushie
import co.akoot.plugins.plushies.util.Items.plushies
import co.akoot.plugins.plushies.util.Util.plushMsg
import org.bukkit.Material
import org.bukkit.command.CommandSender

class PlushieCommand(plugin: FoxPlugin) : FoxCommand(plugin, "plushie", aliases = arrayOf("plush")) {

    override fun onTabComplete(sender: CommandSender, alias: String, args: Array<out String>): MutableList<String> {

        if (args.size == 1) {
            return plushies.map { it.first }.toMutableList()
        } else if (args.size == 2 && plushies.find { it.first.equals(args[0], ignoreCase = true) } != null) {
            return arrayListOf("statue")
        }

        return mutableListOf()
    }

    override fun onCommand(sender: CommandSender, alias: String, args: Array<out String>): Boolean {
        val p = playerCheck(sender) ?: return false

        if (args.isEmpty()) {
            // if no args, open main menu
            p.openInventory(PlushieMenu().inventory)
            return true
        }

        val arg = args[0]

        // reload config
        if (arg in setOf("reload", "load") && hasPermission(sender, "reload")) {
            plushies = plushieConf.getKeys().map { name -> name to (plushieConf.getString(name).takeUnless { it == "0" } ?: name) }
            return sendMessage(sender, "Plushies reloaded!") // good prank!
        }

        val item = p.inventory.itemInMainHand

        val plushie = plushies.find { it.first.equals(arg, ignoreCase = true) }

        return if (plushie != null) {
            val plushName = plushie.first.replace("_.*".toRegex(),"")

            if (item.type != Material.TOTEM_OF_UNDYING) {
                // what did you expect?
                return sendError(sender, "You must be holding a totem!")
            }

            // swap the totem with the super cool new plushie
            val value = if (args.getOrNull(1) == "statue") {
                plushie.second.toIntOrNull()?.plus(1) ?: (plushie.second + ".st")
            } else plushie.second

            p.inventory.setItemInMainHand(createPlushie(plushName, value.toString()))
            p.sendMessage(plushMsg(plushName).component)
            true

        } else {
            // no plushie found
            sendError(sender, "$arg does not exist!")
        }
    }
}
