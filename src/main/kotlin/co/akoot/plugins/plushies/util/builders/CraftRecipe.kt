package co.akoot.plugins.plushies.util.builders

import org.bukkit.Bukkit
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

    private val ingredients = mutableMapOf<Char, RecipeChoice>()
    private val shapelessIngredients = mutableListOf<RecipeChoice>()
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
    fun ingredient(key: Char, input: RecipeChoice): CraftRecipe {
        ingredients[key] = input
        return this
    }

    /**
     * Shapeless Ingredient
     *
     * @param input
     * @return
     */
    fun ingredient(input: RecipeChoice): CraftRecipe {
        shapelessIngredients.add(input)
        return this
    }

    /**
     * Build
     *
     * @return
     */
    fun shapeless(): Recipe {
            val shapelessRecipe = ShapelessRecipe(NamespacedKey("plushies", name), result)

            // Set the ingredients
            shapelessIngredients.forEach { ingredient ->
                shapelessRecipe.addIngredient(ingredient)
            }

            // Register the recipe
            Bukkit.addRecipe(shapelessRecipe)
            return shapelessRecipe
    }

    fun shaped(): Recipe {
            val shapedRecipe = ShapedRecipe(NamespacedKey("plushies", name), result)

            // Set the shape
            shapedRecipe.shape(shape[0], shape[1], shape[2])

            // Set the ingredients
            ingredients.forEach { (key, itemStack) ->
                shapedRecipe.setIngredient(key, itemStack)
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
