package co.akoot.plugins.plushies.util.builders

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.*

/**
 * Craft recipe
 *
 * @property name
 * @property result
 * @constructor
 */
class CraftRecipe private constructor(val name: String, private val result: ItemStack) {

    private val ingredients = mutableMapOf<Char, Any>()
    private val shapelessIngredients = mutableListOf<Any>()
    private val shape = mutableListOf<String>()

    /**
     * Shape
     *
     * @param rows
     * @return
     */
    fun shape(vararg rows: String): CraftRecipe {
        shape.clear()
        shape.addAll(rows)
        return this
    }

    /**
     * Ingredient
     *
     * @param key
     * @param input
     * @return
     */
    fun ingredient(key: Char, material: Material): CraftRecipe {
        ingredients[key] = material
        return this
    }

    fun ingredient(key: Char, choice: RecipeChoice): CraftRecipe {
        ingredients[key] = choice
        return this
    }

    fun ingredient(material: Material, amount: Int = 1): CraftRecipe {
        repeat(amount) { shapelessIngredients.add(material) }
        return this
    }

    fun ingredient(choice: RecipeChoice, amount: Int = 1): CraftRecipe {
        repeat(amount) { shapelessIngredients.add(choice) }
        return this
    }

    /**
     * Build
     *
     * @return
     */
    fun shapeless(plugin: String = "plushies"): Recipe {
        val recipe = NamespacedKey(plugin, name)
        val shapelessRecipe = ShapelessRecipe(recipe, result)

        Bukkit.removeRecipe(recipe)

        // Set the ingredients
        shapelessIngredients.forEach { ingredient ->
            when (ingredient) {
                is Material -> shapelessRecipe.addIngredient(ingredient)
                is RecipeChoice -> shapelessRecipe.addIngredient(ingredient)
            }
        }

        // Register the recipe
        Bukkit.addRecipe(shapelessRecipe)
        return shapelessRecipe
    }

    fun shaped(plugin: String = "plushies"): Recipe {
        val recipe = NamespacedKey(plugin, name)
        val shapedRecipe = ShapedRecipe(recipe, result)

        Bukkit.removeRecipe(recipe)

        // Set the shape
        shapedRecipe.shape(shape[0], shape[1], shape[2])

        // Set the ingredients
        ingredients.forEach { (key, ingredient) ->
            when (ingredient) {
                is Material -> shapedRecipe.setIngredient(key, ingredient)
                is RecipeChoice -> shapedRecipe.setIngredient(key, ingredient)
            }
        }

        // Register the recipe
        Bukkit.addRecipe(shapedRecipe)
        return shapedRecipe
    }

    companion object {
        fun builder(name: String, result: ItemStack): CraftRecipe {
            return CraftRecipe(name, result)
        }
    }
}
