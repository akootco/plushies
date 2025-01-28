package co.akoot.plugins.plushies.util.builders

import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.inventory.*

class CookRecipe private constructor(
    val name: String, private val input: RecipeChoice, private val result: ItemStack, private val cookTime: Int = 200, private val xp: Float = 0.7f) {
    private val recipes = mutableListOf<Recipe>()

    // furnace only
    fun smelt(): CookRecipe {
        val smeltRecipe = SmokingRecipe(
            NamespacedKey("plushies", "${name}_furnace"),
            result, input, xp, cookTime
        )

        // Register the recipe
        Bukkit.addRecipe(smeltRecipe)
        recipes.add(smeltRecipe)
        return this
    }

    // furnace and blast furnace
    fun blast(): CookRecipe {
        val blastRecipe = BlastingRecipe(
            NamespacedKey("plushies", "${name}_blast"),
            result, input, xp, cookTime / 2 // might as well be the same as vanilla
        )

        // Register the recipe
        smelt()
        Bukkit.addRecipe(blastRecipe)
        recipes.add(blastRecipe)
        return this
    }

    // furnace , campfire and smoker
    fun smoke(): CookRecipe {
        val smokerRecipe = SmokingRecipe(
            NamespacedKey("plushies", "${name}_smoker"),
            result, input, xp, cookTime / 2 // also the same as vanilla
        )

        val campfireRecipe = CampfireRecipe(
            NamespacedKey("plushies", "${name}_campfire"),
            result, input, xp, cookTime * 3 // vanilla is 600 ticks, so lets just triple it
        )

        // Register the recipes
        smelt()
        Bukkit.addRecipe(smokerRecipe)
        Bukkit.addRecipe(campfireRecipe)

        recipes.add(smokerRecipe)
        recipes.add(campfireRecipe)
        return this
    }

    companion object {
        fun builder(name: String, input: RecipeChoice, result: ItemStack): CookRecipe {
            return CookRecipe(name, input, result)
        }
    }
}
