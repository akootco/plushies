package co.akoot.plugins.plushies.util.builders

import co.akoot.plugins.bluefox.util.TimeUtil.parseTime
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.inventory.*

class CookRecipe private constructor(
    val name: String, private val input: RecipeChoice, private val result: ItemStack, private val cookTime: String, private val xp: Double) {

    // furnace only
    fun smelt(plugin: String = "plushies"): CookRecipe {

        val recipe = NamespacedKey(plugin, "${name}_furnace")

        Bukkit.removeRecipe(recipe)

        val smeltRecipe = FurnaceRecipe(recipe, result, input, xp.toFloat(), parseTime(cookTime, true).toInt())

        // Register the recipe
        Bukkit.addRecipe(smeltRecipe)
        return this
    }

    // furnace and blast furnace
    fun blast(plugin: String = "plushies"): CookRecipe {
        val recipe = NamespacedKey(plugin, "${name}_blast")

        Bukkit.removeRecipe(recipe)

        val blastRecipe = BlastingRecipe(
            recipe,
            result, input, xp.toFloat(), parseTime(cookTime, true).toInt() / 2 // might as well be the same as vanilla
        )

        // Register the recipe
        smelt(plugin)
        Bukkit.addRecipe(blastRecipe)
        return this
    }

    // furnace , campfire and smoker
    fun smoke(plugin: String = "plushies"): CookRecipe {
        val sRecipe = NamespacedKey(plugin, "${name}_smoker")
        val cfRecipe = NamespacedKey(plugin, "${name}_campfire")


        val smokerRecipe = SmokingRecipe(sRecipe,
            result, input, xp.toFloat(), parseTime(cookTime, true).toInt() / 2 // also the same as vanilla
        )

        val campfireRecipe = CampfireRecipe(cfRecipe,
            result, input, xp.toFloat(), parseTime(cookTime, true).toInt() * 3 // vanilla is 600 ticks, so lets just triple it
        )

        // Register the recipes
        smelt(plugin)
        Bukkit.removeRecipe(sRecipe)
        Bukkit.addRecipe(smokerRecipe)

        Bukkit.removeRecipe(cfRecipe)
        Bukkit.addRecipe(campfireRecipe)

        return this
    }

    companion object {
        fun builder(name: String, input: RecipeChoice, result: ItemStack, cookTime: String? = null, xp: Double? = null): CookRecipe {
            return CookRecipe(name, input, result, cookTime?: "10s", xp?: 0.7)
        }
    }
}
