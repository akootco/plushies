package co.akoot.plugins.plushies.api

import co.akoot.plugins.bluefox.api.FoxConfig
import co.akoot.plugins.plushies.util.Recipes.getInput
import co.akoot.plugins.plushies.util.Recipes.getMaterial
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.RecipeChoice

data class SimpleRecipe(
    val tool: RecipeChoice?,
    val input: RecipeChoice,
    val results: Set<ItemStack>
)

fun loadSimpleRecipes(
    config: FoxConfig
): MutableList<SimpleRecipe> {
    val recipes = mutableListOf<SimpleRecipe>()

    for (r in config.getKeys()) {
        val toolInput = config.getString("$r.tool")?.let {
            getInput(it)
        }

        val inputItem = getInput(config.getString("$r.input") ?: continue)
            ?: continue

        val results = parseResults(config.getStringList("$r.result"))

        if (results.isEmpty()) continue

        recipes.add(SimpleRecipe(toolInput, inputItem, results))
    }

    return recipes
}

fun parseResults(stringList: List<String>): Set<ItemStack> {
    val results = mutableSetOf<ItemStack>()

    for (string in stringList) {
        val split = string.split("/")
        val item = split[0]
        val amount = split.getOrNull(1)?.toIntOrNull() ?: 1
        val material = getMaterial(item, amount) ?: continue

        results.add(material)
    }

    return results
}

