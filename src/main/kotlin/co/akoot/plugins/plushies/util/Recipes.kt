package co.akoot.plugins.plushies.util

import co.akoot.plugins.plushies.Plushies.Companion.cookRecipeConf
import co.akoot.plugins.plushies.Plushies.Companion.key
import co.akoot.plugins.plushies.Plushies.Companion.recipeConf
import co.akoot.plugins.plushies.util.Items.customItems
import co.akoot.plugins.plushies.util.builders.CookRecipe
import co.akoot.plugins.plushies.util.builders.CraftRecipe
import com.destroystokyo.paper.MaterialTags
import net.kyori.adventure.text.logger.slf4j.ComponentLogger.logger
import org.bukkit.Bukkit
import org.bukkit.Keyed
import org.bukkit.Material
import org.bukkit.Tag
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.RecipeChoice
import org.bukkit.inventory.RecipeChoice.MaterialChoice
import org.bukkit.inventory.StonecuttingRecipe

object Recipes {

    fun registerPlushieRecipes() {
        coloredRecipes()
        woodCutterRecipes()
        strippedWoodRecipe()
        configRecipes()
        smeltingRecipes()
    }

    // get recipe input items
    private fun getInput(input: String): RecipeChoice? {
        // if no prefix, check for custom item or vanilla material.
        customItems.keys.find { it.equals(input, ignoreCase = true) }?.let { key ->
            customItems[key]?.let {
                return RecipeChoice.ExactChoice(it)
            }
        }
        Material.getMaterial(input.uppercase())?.let { return MaterialChoice(it) }
        return null
    }

    fun getMaterial(input: String, amount: Int = 1): ItemStack? {
        // if no prefix, check for flugin item or vanilla material.
        customItems.keys.find { it.equals(input, ignoreCase = true) }?.let { key ->
            customItems[key]?.let {
                it.amount = amount
                return it
            }
        }

        Material.getMaterial(input.uppercase())?.let {
            val itemStack = ItemStack(it)
            itemStack.amount = amount
            return itemStack
        }

        return null
    }

    fun unlockRecipes(player: Player, id: String = "plushies") {
        val iterator = Bukkit.recipeIterator()
        while (iterator.hasNext()) {
            val recipe = iterator.next()

            if (recipe is Keyed) {
                val key = (recipe as Keyed).key // erm?
                if (key.namespace == id) {
                    player.discoverRecipe(key)
                }
            }
        }
    }

    private fun strippedWoodRecipe() {
        Tag.LOGS.values.plus(Material.BAMBOO_BLOCK).forEach { inputMaterial -> // erm!!!! .plus is cool!
            val resultMaterial = "STRIPPED_" + inputMaterial.name

            Material.entries.find { it.name == resultMaterial }?.let { output ->
                Bukkit.addRecipe(StonecuttingRecipe(key( output.name.lowercase()),
                        ItemStack(output), MaterialChoice(inputMaterial)))
            }
        }
    }

    private fun coloredRecipes() {
        val recipes = mapOf(
            "_TERRACOTTA" to Tag.TERRACOTTA,
            "_GLAZED_TERRACOTTA" to MaterialTags.GLAZED_TERRACOTTA,
            "_CONCRETE" to MaterialTags.CONCRETES,
            "_CONCRETE_POWDER" to MaterialTags.CONCRETE_POWDER,
            "_STAINED_GLASS" to MaterialTags.STAINED_GLASS,
            "_STAINED_GLASS_PANE" to MaterialTags.STAINED_GLASS_PANES,
            "_WOOL" to Tag.WOOL,
            "_CARPET" to Tag.WOOL_CARPETS
        )

        recipes.forEach { (suffix, tag) ->
            val terr = Material.entries
                .filter { it.name.endsWith("_DYE") }
                .associateWith { Material.valueOf(it.name.replace("_DYE", suffix)) }

            terr.forEach { (dye: Material, item: Material) ->
                CraftRecipe.builder(item.name.lowercase(), ItemStack(item, 8))
                    .shape("TTT", "TDT", "TTT")
                    .ingredient('T', MaterialChoice(tag))
                    .ingredient('D', MaterialChoice(dye))
                    .shaped()
            }
        }
    }

    private fun woodCutterRecipes() {
        val output = mapOf(
            "_SLAB" to 2,
            "_STAIRS" to 1,
            "_FENCE_GATE" to 2, // this is so OP!
            "_FENCE" to 2,
            "_TRAPDOOR" to 3,
            "_PRESSURE_PLATE" to 4,
            "_SIGN" to 2,
        )
        // they made a whole plugin for this?
        Tag.PLANKS.values.forEach { plank ->
            output.forEach { (suffix, count) ->
                val resultName = plank.name.replace("_PLANKS", suffix)
                // if output matches a material, create the recipe
                Material.entries.find { it.name == resultName }?.let { resultMaterial ->
                    Bukkit.addRecipe(StonecuttingRecipe(
                        key( resultMaterial.name.lowercase()),
                        ItemStack(resultMaterial, count),
                        MaterialChoice(plank)))
                }
            }
        }
    }

    private fun smeltingRecipes() {
        for (key in cookRecipeConf.getKeys()) {
            val result = cookRecipeConf.getString("$key.output") ?: return
            val input = cookRecipeConf.getString("$key.input") ?: return

            val parts = result.split("/")

            CookRecipe.builder(
                key,
                getInput(input) ?: return,
                getMaterial(parts[0]) ?: return,
                cookRecipeConf.getString("$key.cookTime"),
                cookRecipeConf.getDouble("$key.xp")
            ).apply {
                cookRecipeConf.getStringList("$key.type").forEach { type ->
                    when (type.lowercase()) {
                        "smoke" -> smoke() // smoker / campfire
                        "blast" -> blast()
                        "all" -> blast().smoke() // blast furnace
                    }
                }
            }.smelt()
        }
    }

    private fun configRecipes() {
        for (key in recipeConf.getKeys()) {
            val shape = recipeConf.getStringList("$key.shape")
            val result = recipeConf.getString("$key.result") ?: continue

            if (shape.isEmpty()) {
                // shapeless recipe
                CraftRecipe.builder(key, ItemStack(getMaterial(result) ?: continue))// skip if output is invalid
                    .apply {
                        for (ingredient in recipeConf.getStringList("$key.ingredients")) {
                            val parts = ingredient.split("/")
                            val amount = parts.getOrNull(1)?.toIntOrNull() ?: 1

                            val material = getInput(parts[0]) ?: continue // skip if input is invalid

                            // nice!, add ingredient to recipe
                            ingredient(material, amount)
                        }
                    }.shapeless()
            } else {

                if (shape.size != 3)  {
                    logger("Plushies").warn("Invalid shape for recipe $key")
                    continue // skip if shape is invalid
                }

                CraftRecipe.builder(key, ItemStack(getMaterial(result) ?: continue)) // skip if output is invalid
                    .shape(shape[0], shape[1], shape[2])
                    .apply {
                        for (ingredient in recipeConf.getKeys("$key.ingredients")) {
                            val material = recipeConf.getString("$key.ingredients.$ingredient")
                                ?.let { getInput(it) } ?: continue // skip if input is invalid

                            // all valid, add ingredient
                            ingredient(ingredient[0], material)
                        }
                    }.shaped()
            }
        }
    }
}
