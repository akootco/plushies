package co.akoot.plugins.plushies.util

import co.akoot.plugins.plushies.Plushies.Companion.recipeConf
import co.akoot.plugins.plushies.util.builders.CraftRecipe
import com.destroystokyo.paper.MaterialTags
import net.kyori.adventure.text.logger.slf4j.ComponentLogger.logger
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.Tag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.RecipeChoice.MaterialChoice
import org.bukkit.inventory.StonecuttingRecipe

object Recipes {

    fun registerRecipes() {
        coloredRecipes()
        woodCutterRecipes()
        strippedWoodRecipe()
        configRecipes()
    }

    private fun strippedWoodRecipe() {
        Tag.LOGS.values.plus(Material.BAMBOO_BLOCK).forEach { inputMaterial -> // erm!!!! .plus is cool!
            val resultMaterial = "STRIPPED_" + inputMaterial.name

            Material.entries.find { it.name == resultMaterial }?.let { output ->
                Bukkit.addRecipe(StonecuttingRecipe(NamespacedKey("plushies", output.name.lowercase()),
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
                        NamespacedKey("plushies", resultMaterial.name.lowercase()),
                        ItemStack(resultMaterial, count),
                        MaterialChoice(plank)))
                }
            }
        }
    }

    private fun configRecipes() {
        for (key in recipeConf.getKeys()) {
            val shape = recipeConf.getStringList("$key.shape")
            val result = recipeConf.getString("$key.result") ?: continue

            if (shape.isEmpty()) {
                // shapeless recipe
                CraftRecipe.builder(key, ItemStack(Material.matchMaterial(result) ?: continue))// skip if output is invalid
                    .apply {
                        for (ingredient in recipeConf.getStringList("$key.ingredients")) {
                            val parts = ingredient.split("/")
                            val amount = parts.getOrNull(1)?.toIntOrNull() ?: 1

                            val material = Material.matchMaterial(parts[0]) ?: continue // skip if input is invalid

                            // nice!, add ingredient to recipe
                            ingredient(MaterialChoice(material), amount)
                        }
                    }.shapeless()
            } else {

                if (shape.size != 3)  {
                    logger("Plushies").warn("Invalid shape for recipe $key")
                    continue // skip if shape is invalid
                }

                CraftRecipe.builder(key, ItemStack(Material.matchMaterial(result) ?: continue)) // skip if output is invalid
                    .shape(shape[0], shape[1], shape[2])
                    .apply {
                        for (ingredient in recipeConf.getKeys("$key.ingredients")) {
                            val material = recipeConf.getString("$key.ingredients.$ingredient")
                                ?.let { Material.matchMaterial(it) } ?: continue // skip if input is invalid

                            // all valid, add ingredient
                            ingredient(ingredient[0], MaterialChoice(material))
                        }
                    }.shaped()
            }
        }
    }
}
