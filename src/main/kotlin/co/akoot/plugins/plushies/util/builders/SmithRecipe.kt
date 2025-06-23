package co.akoot.plugins.plushies.util.builders

import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.inventory.*

class SmithRecipe private constructor(
    val name: String, private val template: RecipeChoice, private val base: RecipeChoice, private val addition: RecipeChoice, private val result: ItemStack) {

    fun add(plugin: String = "plushies"): SmithRecipe {

        val recipe = NamespacedKey(plugin, name)

        Bukkit.removeRecipe(recipe)

        val smithRecipe = SmithingTransformRecipe(recipe, result, template, base, addition)

        // Register the recipe
        Bukkit.addRecipe(smithRecipe)
        return this
    }

    companion object {
        fun builder(name: String, template: RecipeChoice, base: RecipeChoice, addition: RecipeChoice, result: ItemStack): SmithRecipe {
            return SmithRecipe(name, template, base, addition, result)
        }
    }
}
