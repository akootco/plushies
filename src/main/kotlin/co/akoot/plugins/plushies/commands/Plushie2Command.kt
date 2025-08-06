package co.akoot.plugins.plushies.commands

import co.akoot.plugins.bluefox.api.FoxCommand
import co.akoot.plugins.bluefox.api.FoxPlugin
import co.akoot.plugins.plushies.util.Items.createPlushie
import co.akoot.plugins.plushies.util.Items.plushies
import co.akoot.plugins.plushies.util.builders.DialogBuilder
import org.bukkit.command.CommandSender

class Plushie2Command(plugin: FoxPlugin) : FoxCommand(plugin, "plushie2", aliases = arrayOf("plush2")) {

    override fun onTabComplete(sender: CommandSender, alias: String, args: Array<out String>): MutableList<String> {
        return mutableListOf()
    }

    override fun onCommand(sender: CommandSender, alias: String, args: Array<out String>): Boolean {
        val p = playerCheck(sender) ?: return false

        val plushieDialog = DialogBuilder()
            .apply { plushies.forEach {
                icon(createPlushie(it.first.replace("_.*".toRegex(),""), it.second))
            } }.build()

        p.showDialog(plushieDialog)
        return true
    }
}
