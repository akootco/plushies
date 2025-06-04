package co.akoot.plugins.plushies.commands

import co.akoot.plugins.bluefox.api.FoxCommand
import co.akoot.plugins.bluefox.api.FoxPlugin
import co.akoot.plugins.plushies.Plushies.Companion.plushieConf
import co.akoot.plugins.plushies.util.Items.createPlushie
import co.akoot.plugins.plushies.util.Items.plushies
import co.akoot.plugins.plushies.gui.PlushieMenu
import co.akoot.plugins.plushies.util.Util.plushMsg
import org.bukkit.*
import org.bukkit.command.CommandSender

class PlushieCommand(plugin: FoxPlugin) : FoxCommand(plugin, "plushie", aliases = arrayOf("plushies", "plush")) {

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
            plushies = plushieConf.getKeys().mapNotNull { name -> plushieConf.getInt(name)?.let { name to it } }
            return sendMessage(sender, "Plushies reloaded!") // good prank!
        }

        val item = p.inventory.itemInMainHand

        val plushie = plushies.find { it.first.equals(arg, ignoreCase = true) }

        return if (plushie != null) {
            val plushName = plushie.first.replace("_.*".toRegex(),"")

            if (p.gameMode == GameMode.CREATIVE) {
                p.inventory.addItem(createPlushie(plushName, plushie.second))
                p.sendMessage(plushMsg(plushName).component)
                return true
            }


            else if (item.type != Material.TOTEM_OF_UNDYING) {
                // what did you expect?
                return sendError(sender, "You must be holding a totem!")
            }

            // swap the totem with the super cool new plushie
            p.inventory.setItemInMainHand(createPlushie(plushName, if (args.getOrNull(1) == "statue") plushie.second + 1 else plushie.second))
            p.sendMessage(plushMsg(plushName).component)
            true

        } else {
            // no plushie found
            sendError(sender, "$arg does not exist!")
        }
    }
}
