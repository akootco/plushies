package co.akoot.plugins.plushies.util.brewery

import co.akoot.plugins.bluefox.util.Text
import co.akoot.plugins.plushies.Plushies.Companion.checkPlugin
import co.akoot.plugins.plushies.util.builders.ItemBuilder
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.inventory.ItemStack
import java.io.File

object Brew {

    var breweryConfig: YamlConfiguration? = null

    private val regex = Regex("Edulis:|Brewery:|[\\[\\]]|&[0-9a-fk-or]")

    private val woodMap = mapOf(
        0 to "Any",
        1 to "Birch",
        2 to "Oak",
        3 to "Jungle",
        4 to "Spruce",
        5 to "Acacia",
        6 to "Dark Oak",
        7 to "Crimson",
        8 to "Warped",
        9 to "Mangrove",
        10 to "Cherry",
        11 to "Bamboo",
        12 to "Pale Oak"
    )

    init {
        val brewery = checkPlugin("Brewery")
        if (brewery != null) {
            breweryConfig = YamlConfiguration.loadConfiguration(File(brewery.dataFolder, "config.yml"))
        }
    }

    fun brewBook(recipe: String): ItemStack? {
        val pages = mutableListOf<Component>()

        val recipeSection = breweryConfig?.getConfigurationSection("recipes") ?: return null

        if (recipe == "all") {
            // create book with all recipes
            for (key in recipeSection.getKeys(false)) {
                createPage(key)?.let { pages.add(it) }
            }

            return ItemBuilder.builder(ItemStack(Material.WRITTEN_BOOK))
                .writtenBook(pages, "Brewery", generation = 3)
                .build()
        } else {
            // create book with single recipe
            return ItemBuilder.builder(ItemStack(Material.WRITTEN_BOOK))
                .writtenBook(createPage(recipe) ?: return null)
                .build()
        }
    }

    private fun createPage(recipe: String): Component? {
        val section = breweryConfig?.getConfigurationSection("recipes.$recipe") ?: return null
        val ingredients = section.getStringList("ingredients")
        val name = section.getString("name")?.split("/")?.last()?.replace(regex, "") ?: section.name

        val result = (Text(name).plus("\n\n")
                + Text(ingredients.toString()
                    .replace(regex, "")
                    .replace(", ", "\n")
                    .replace("_", " ")
                    .lowercase())
                + Text("\n\nCooking Time: ") + Text(section.getInt("cookingtime").toString()))

        if (section.contains("wood")) {
            result + Text("\nWood: ").plus(woodMap[section.getInt("wood")] ?: "error")
        }

        if (section.contains("age") && section.getInt("age") != 0) {
            result + Text("\nAge: ").plus(section.getInt("age").toString())
        }

        if (section.contains("distillruns") && section.getInt("distillruns") != 0) {
            result + Text("\nDistill Runs: ").plus(section.getInt("distillruns").toString())
        }

        return result.component
    }
}


