package co.akoot.plugins.plushies.util

import co.akoot.plugins.plushies.util.builders.CraftRecipe
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.Tag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.RecipeChoice.MaterialChoice
import org.bukkit.inventory.StonecuttingRecipe
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.forEach

object Recipes {


    fun registerRecipes() {
        terracottaRecipes()
        coloredRecipes()
        woodCutterRecipes()
    }

    private fun terracottaRecipes() {
        val terr = Material.entries
            .filter { it.name.endsWith("_DYE") }
            .associateWith { Material.valueOf(it.name.replace("_DYE", "_TERRACOTTA")) }

        terr.forEach { (dye: Material, terracotta: Material) ->
            CraftRecipe.builder(terracotta.name.lowercase(), ItemStack(terracotta, 8))
                .shape("TTT", "TDT", "TTT")
                .ingredient('T', MaterialChoice(Tag.TERRACOTTA))
                .ingredient('D', MaterialChoice(dye))
                .shaped()
        }
    }

    private fun coloredRecipes() {
        val recipeTypes = listOf("_STAINED_GLASS", "_CONCRETE", "_CONCRETE_POWDER", "_GLAZED_TERRACOTTA")

        recipeTypes.forEach { suffix ->
            val materials = Material.entries
                .filter { it.name.endsWith("_DYE") }
                .associateWith { Material.valueOf(it.name.replace("_DYE", suffix)) }

            val baseMaterials = Material.entries.filter { it.name.endsWith(suffix) }.toTypedArray()

            materials.forEach { (dye, resultMaterial) ->
                CraftRecipe.builder(resultMaterial.name.lowercase(), ItemStack(resultMaterial, 8))
                    .shape("AAA", "ABA", "AAA")
                    .ingredient('A', MaterialChoice(*baseMaterials))
                    .ingredient('B', MaterialChoice(dye))
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
        Material.entries.filter { it.name.endsWith("_PLANKS") }.forEach { plank ->

            output.forEach { (suffix, count) ->
                val resultName = plank.name.replace("_PLANKS", suffix)
                // if output matches a material, create the recipe
                Material.entries.find { it.name == resultName }?.let { resultMaterial ->
                    Bukkit.addRecipe(StonecuttingRecipe(NamespacedKey("plushies",
                        resultMaterial.name.lowercase()),
                        ItemStack(resultMaterial, count),
                        MaterialChoice(plank)))
                }
            }
        }
    }
}
