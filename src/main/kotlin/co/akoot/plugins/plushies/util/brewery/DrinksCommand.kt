package co.akoot.plugins.plushies.util.brewery

import co.akoot.plugins.bluefox.api.FoxCommand
import co.akoot.plugins.bluefox.api.FoxPlugin
import co.akoot.plugins.plushies.util.brewery.Brew.brewBook
import co.akoot.plugins.plushies.util.brewery.Brew.getBreweryConfig
import org.bukkit.command.CommandSender

class DrinksCommand(plugin: FoxPlugin) : FoxCommand(plugin, "drinks") {

    override fun onTabComplete(sender: CommandSender, alias: String, args: Array<out String>): MutableList<String> {

        if (args.size == 1) {
            val keys = getBreweryConfig()?.getConfigurationSection("recipes")?.getKeys(false)
                ?: return mutableListOf()

            return keys.toMutableList()
        }

        return mutableListOf()
    }

    override fun onCommand(sender: CommandSender, alias: String, args: Array<out String>): Boolean {
        val p = playerCheck(sender) ?: return false

        val recipe = if (args.isNotEmpty()) args[0] else "all"
        val book = brewBook(recipe) ?: return sendError(p, "Recipe not found!")

        if (recipe == "book" && permissionCheck(p, "book") == true) { p.give(book) }
        else { p.openBook(book) }

        return true
    }
}