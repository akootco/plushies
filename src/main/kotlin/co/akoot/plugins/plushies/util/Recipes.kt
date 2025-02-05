package co.akoot.plugins.plushies.util

import co.akoot.plugins.plushies.util.builders.CraftRecipe
import org.bukkit.Material
import org.bukkit.Tag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.RecipeChoice
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.forEach

object Recipes {

    fun terracottaRecipes() {
        val terr = Material.entries
            .filter { it.name.endsWith("_DYE") }
            .associateWith { Material.valueOf(it.name.replace("_DYE", "_TERRACOTTA")) }

        terr.forEach { (dye: Material, terracotta: Material) ->
            CraftRecipe.builder(terracotta.name.lowercase(), ItemStack(terracotta, 8))
                .shape("TTT", "TDT", "TTT")
                .ingredient('T', RecipeChoice.MaterialChoice(Tag.TERRACOTTA))
                .ingredient('D', RecipeChoice.MaterialChoice(dye))
                .shaped()
        }
    }

    fun glassRecipes() {
        val glass = Material.entries
            .filter { it.name.endsWith("_DYE") }
            .associateWith { Material.valueOf(it.name.replace("_DYE", "_STAINED_GLASS")) }

        val stainedGlassMaterials = Material.entries.filter { it.name.endsWith("_STAINED_GLASS") }.toTypedArray()

        glass.forEach { (dye, stainedGlass) ->
            CraftRecipe.builder(stainedGlass.name.lowercase(), ItemStack(stainedGlass, 8))
                .shape("GGG", "GDG", "GGG")
                .ingredient('G', RecipeChoice.MaterialChoice(*stainedGlassMaterials))
                .ingredient('D', RecipeChoice.MaterialChoice(dye))
                .shaped()
        }
    }
}
