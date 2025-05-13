package co.akoot.plugins.plushies.commands

import co.akoot.plugins.bluefox.api.FoxCommand
import co.akoot.plugins.bluefox.api.FoxPlugin
import co.akoot.plugins.plushies.util.Recipes.registerRecipes
import org.bukkit.Bukkit
import org.bukkit.Keyed
import org.bukkit.command.CommandSender

class ReloadRecipesCommand(plugin: FoxPlugin) : FoxCommand(plugin, "reloadrecipes", description = "reloads plushies recipes") {

    override fun onTabComplete(sender: CommandSender, alias: String, args: Array<out String>): MutableList<String> {
        return mutableListOf()
    }

    override fun onCommand(sender: CommandSender, alias: String, args: Array<out String>): Boolean {

        // remove all recipes
        val iterator = Bukkit.recipeIterator()
        while (iterator.hasNext()) {
            val recipe = iterator.next()

            if (recipe is Keyed) {
                val key = (recipe as Keyed).key // erm?
                if (key.namespace == "plushies") {
                    Bukkit.removeRecipe(key)
                }
            }
        }

        registerRecipes()
        return sendMessage(sender, "Plushie Recipes reloaded!")
    }
}