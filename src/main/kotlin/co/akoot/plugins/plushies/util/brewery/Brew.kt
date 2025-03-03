package co.akoot.plugins.plushies.util.brewery

import co.akoot.plugins.plushies.Plushies.Companion.checkPlugin
import co.akoot.plugins.plushies.util.builders.ItemBuilder
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.Material
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.inventory.ItemStack
import java.io.File

object Brew {

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

    private var breweryConfig: YamlConfiguration? = null

    init {
        val brewery = checkPlugin("Brewery")
        if (brewery != null) {
            breweryConfig = YamlConfiguration.loadConfiguration(File(brewery.dataFolder, "config.yml"))
        }
    }

    fun getBreweryConfig(): YamlConfiguration? {
        return breweryConfig
    }

    fun brewBook(recipe: String): ItemStack? {
        val pages = mutableListOf<Component>()

        val recipeSection = getBreweryConfig()?.getConfigurationSection("recipes") ?: return null

        if (recipe == "all") {
            // create book with all recipes
            for (key in recipeSection.getKeys(false)) {
                addPage(key)?.let { pages.add(it) }
            }
            return ItemBuilder.builder(ItemStack(Material.WRITTEN_BOOK))
                .writtenBook(pages)
                .build()
        } else {
            // create book with single recipe
            val page = addPage(recipe) ?: return null
            return ItemBuilder.builder(ItemStack(Material.WRITTEN_BOOK))
                .writtenBook(page)
                .build()
        }
    }

    private fun addPage(recipe: String): Component? {
        val section = breweryConfig?.getConfigurationSection("recipes.$recipe") ?: return null

        val page = StringBuilder()

        page.append(section.name)

        val ingredients = section.getStringList("ingredients")

        if (ingredients.isNotEmpty()) {
            val ingredientsString = ingredients.toString()
                .replace(Regex("Edulis:|Brewery:|[\\[\\]]"), "")
                .replace(", ", "&r\n")

            page.append("\n\n$ingredientsString").append("&r")
        }


        // set cook time, default to 2
        val cookingTime = section.getInt("cookingtime", 2)
        page.append("\n\nCooking Time: ").append(cookingTime)

        // recipes don't require aging, but if they do, show it
        if (section.contains("wood")) {
            val wood = section.getInt("wood")
            page.append("\nWood: ").append(woodMap[wood])
        }

        // same thing here, not required
        if (section.contains("age")) {
            val age = section.getInt("age")
            if (age != 0) {
                page.append("\nAge: ").append(age)
            }
        }

        // same here
        if (section.contains("distillruns")) {
            val distRuns = section.getInt("distillruns")
            if (distRuns != 0) {
                page.append("\nDistill Runs: ").append(distRuns)
            }
        }

        // return the page with color
        return LegacyComponentSerializer.legacyAmpersand().deserialize(page.toString())
    }
}


