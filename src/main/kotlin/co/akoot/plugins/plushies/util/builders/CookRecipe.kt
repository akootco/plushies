package co.akoot.plugins.plushies.util.builders

import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.inventory.*

class CookRecipe private constructor(
    val name: String, private val input: RecipeChoice, private val result: ItemStack, private val cookTime: Int = 200, private val xp: Float = 0.7f) {
    private val recipes = mutableListOf<Recipe>()

    // furnace only
    fun smelt(plugin: String = "plushies"): CookRecipe {

        val recipe = NamespacedKey(plugin, "${name}_furnace")

        Bukkit.removeRecipe(recipe)

        val smeltRecipe = FurnaceRecipe(recipe, result, input, xp, cookTime)

        // Register the recipe
        Bukkit.addRecipe(smeltRecipe)
        recipes.add(smeltRecipe)
        return this
    }

    // furnace and blast furnace
    fun blast(plugin: String = "plushies"): CookRecipe {
        val recipe = NamespacedKey(plugin, "${name}_blast")

        Bukkit.removeRecipe(recipe)

        val blastRecipe = BlastingRecipe(
            recipe,
            result, input, xp, cookTime / 2 // might as well be the same as vanilla
        )

        // Register the recipe
        smelt(plugin)
        Bukkit.addRecipe(blastRecipe)
        recipes.add(blastRecipe)
        return this
    }

    // furnace , campfire and smoker
    fun smoke(plugin: String = "plushies"): CookRecipe {
        val sRecipe = NamespacedKey(plugin, "${name}_smoker")
        val cfRecipe = NamespacedKey(plugin, "${name}_campfire")


        val smokerRecipe = SmokingRecipe(sRecipe,
            result, input, xp, cookTime / 2 // also the same as vanilla
        )

        val campfireRecipe = CampfireRecipe(cfRecipe,
            result, input, xp, cookTime * 3 // vanilla is 600 ticks, so lets just triple it
        )

        // Register the recipes
        smelt(plugin)
        Bukkit.removeRecipe(sRecipe)
        Bukkit.addRecipe(smokerRecipe)

        Bukkit.removeRecipe(cfRecipe)
        Bukkit.addRecipe(campfireRecipe)

        recipes.add(smokerRecipe)
        recipes.add(campfireRecipe)
        return this
    }

    companion object {
        fun builder(name: String, input: RecipeChoice, result: ItemStack, cookTime: Int = 200, xp: Float = 0.7f): CookRecipe {
            return CookRecipe(name, input, result, cookTime, xp)
        }
    }
}
