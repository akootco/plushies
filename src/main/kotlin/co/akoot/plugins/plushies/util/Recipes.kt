package co.akoot.plugins.plushies.util

import co.akoot.plugins.bluefox.util.Txt
import co.akoot.plugins.plushies.util.builders.CookRecipe
import co.akoot.plugins.plushies.util.builders.CraftRecipe
import co.akoot.plugins.plushies.util.builders.ItemBuilder
import org.bukkit.Material
import org.bukkit.Tag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.RecipeChoice
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.forEach

object Recipes {

    fun addRecipes() {
        terracottaRecipes()
        glassRecipes()
        charcoal()

        CraftRecipe.builder("enchanted_golden_apple", ItemStack(Material.ENCHANTED_GOLDEN_APPLE)) // as god intended
            .shape("AAA", "ABA", "AAA")
            .ingredient('A', RecipeChoice.MaterialChoice(Material.GOLD_BLOCK))
            .ingredient('B', RecipeChoice.MaterialChoice(Material.APPLE))
            .shaped()

        CraftRecipe.builder("shulker_box", ItemStack(Material.SHULKER_BOX))
            .ingredient(RecipeChoice.MaterialChoice(Material.SHULKER_SHELL))
            .ingredient(RecipeChoice.MaterialChoice(Material.SHULKER_SHELL))
            .ingredient(RecipeChoice.MaterialChoice(Material.CHEST))
            .shapeless()

        CookRecipe.builder("smoker_test", RecipeChoice.MaterialChoice(Material.DIAMOND), ItemStack(Material.ENCHANTED_GOLDEN_APPLE))
            .smoke()
    }

    private fun terracottaRecipes() {
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

    private fun glassRecipes() {
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

    // ill probably move this over to food plugin, not sure yet
    private fun charcoal() {
        val charcoalBlock = ItemBuilder.builder(ItemStack(Material.COAL_BLOCK))
            .itemName(Txt("Charcoal Block").c) // alces will make sure the name doesn't change
            .build()

        CraftRecipe.builder("charcoal_block", charcoalBlock)
            .shape("AAA", "AAA", "AAA")
            .ingredient('A', RecipeChoice.MaterialChoice(Material.CHARCOAL))
            .shaped()

        CraftRecipe.builder("charcoal", ItemStack(Material.CHARCOAL, 9))
            .ingredient(RecipeChoice.ExactChoice(charcoalBlock))
            .shapeless()
    }
}
