package co.akoot.plugins.plushies.util

import co.akoot.plugins.bluefox.api.FoxConfig
import co.akoot.plugins.bluefox.api.Kolor
import co.akoot.plugins.bluefox.util.Text
import co.akoot.plugins.plushies.Plushies.Companion.cookRecipeConf
import co.akoot.plugins.plushies.Plushies.Companion.key
import co.akoot.plugins.plushies.Plushies.Companion.recipeConf
import co.akoot.plugins.plushies.Plushies.Companion.smithRecipeConf
import co.akoot.plugins.plushies.util.Items.customItems
import co.akoot.plugins.plushies.util.builders.CookRecipe
import co.akoot.plugins.plushies.util.builders.CraftRecipe
import co.akoot.plugins.plushies.util.builders.ItemBuilder
import co.akoot.plugins.plushies.util.builders.SmithRecipe
import com.destroystokyo.paper.MaterialTags
import io.papermc.paper.registry.RegistryKey
import io.papermc.paper.registry.tag.TagKey
import net.kyori.adventure.text.logger.slf4j.ComponentLogger.logger
import org.bukkit.*
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.RecipeChoice
import org.bukkit.inventory.RecipeChoice.MaterialChoice
import org.bukkit.inventory.StonecuttingRecipe

object Recipes {

    fun registerPlushieRecipes() {
        woodCutterRecipes()
        strippedWoodRecipe()
        configRecipes()
        smeltingRecipes()
        smithingRecipes()
        dyeRecipes()
        shulkers() // why is nobody licking my brains?!?
        coloredShulker()
        deepslate()

        CraftRecipe.builder("wrench", customItems["wrench"]?: return)
            .ingredient(Material.LIGHTNING_ROD)
            .ingredient(Material.COPPER_INGOT)
            .shapeless()
    }

    fun tag(tag: String): RecipeChoice? {
        val key = NamespacedKey.minecraft(tag.trim().lowercase())
        val tagKey = TagKey.create(RegistryKey.ITEM, key)
        val tag = Registry.ITEM.getTag(tagKey)

        return if (tag.isEmpty) null else RecipeChoice.itemType(tag)
    }

    fun getInput(input: String): RecipeChoice? {
        if (input.startsWith("tag.")) { return tag(input.substring(4)) }
        customItems[input.lowercase()]?.let { return RecipeChoice.ExactChoice(it) }
        return Material.getMaterial(input.uppercase())?.asItemType()?.let { RecipeChoice.itemType(it) }
    }

    fun getMaterial(input: String, amount: Int = 1): ItemStack? {
        // if no prefix, check for flugin item or vanilla material.
        customItems[input.lowercase()]?.let {
                val copy = it.clone()  // clone first smh
                copy.amount = amount
                return copy
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
        Tag.LOGS.values.plus(Material.BAMBOO_BLOCK).forEach { input ->
            val output = Material.entries.find { it.name == "STRIPPED_${input.name}" } ?: return@forEach

            Bukkit.addRecipe(
                StonecuttingRecipe(
                    key(output.name.lowercase()),
                    ItemStack(output),
                    input
                )
            )
        }
    }

    private fun dyeRecipes() {
        MaterialTags.DYES.values.forEach { dye ->
            val color = dye.name.removeSuffix("_DYE")

            CraftRecipe.builder(
                "${color.lowercase()}.elytra",
                ItemBuilder(Material.ELYTRA).dye(DyeColor.valueOf(color).color).build())
                .ingredient(Material.ELYTRA)
                .ingredient(dye)
                .shapeless()

            val placeholder = ItemBuilder(Material.STRUCTURE_VOID)
                .itemName(Text("Furniture").component)
                .lore(listOf(Kolor.QUOTE("PLACEHOLDER").bold().component))
                .customModelData("couch")
                .dye(DyeColor.valueOf(color).color).build()

            CraftRecipe.builder(
                "${color.lowercase()}.furniture_placeholder",
                placeholder)
                .ingredient(Material.STRUCTURE_VOID)
                .ingredient(dye)
                .shapeless()
        }
    }

    private fun coloredShulker() {
        MaterialTags.DYES.values.forEach { dye ->
            val color = dye.name.removeSuffix("_DYE")
            val output = getMaterial("${color.lowercase()}_shulker_box", 1) ?: return@forEach

            CraftRecipe.builder("${color.lowercase()}.shulker", output)
                .ingredient(Material.CHEST)
                .ingredient(Material.SHULKER_SHELL, 2)
                .ingredient(dye)
                .shapeless()
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
                val resultMaterial = Material.entries.find { it.name == resultName } ?: return@forEach

                Bukkit.addRecipe(
                    StonecuttingRecipe(
                        key(resultMaterial.name.lowercase()),
                        ItemStack(resultMaterial, count),
                        plank
                    )
                )
            }
        }

        Bukkit.addRecipe(
            StonecuttingRecipe(
                key("quartz_pillar_slab"),
                ItemStack(Material.QUARTZ_SLAB, 2),
                Material.QUARTZ_PILLAR
            )
        )

        Bukkit.addRecipe(
            StonecuttingRecipe(
                key("quartz_pillar_stairs"),
                ItemStack(Material.QUARTZ_STAIRS),
                Material.QUARTZ_PILLAR
            )
        )
    }

    private fun deepslate() {
        Bukkit.recipeIterator().forEach { recipe ->
            if (recipe is StonecuttingRecipe && recipe.inputChoice is MaterialChoice) {
                val inputChoice = recipe.inputChoice as MaterialChoice
                if (inputChoice.choices.any { it.name.startsWith("COBBLED_DEEPSLATE") }) {
                    val result = recipe.result.clone()
                    val key = NamespacedKey("plushies", result.type.name.lowercase())

                    Bukkit.addRecipe(StonecuttingRecipe(key, result, Material.DEEPSLATE))
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

    private fun smithingRecipes() {
        for (key in smithRecipeConf.getKeys()) {
            val result = smithRecipeConf.getString("$key.output") ?: return
            val base = smithRecipeConf.getString("$key.base") ?: return
            val template = smithRecipeConf.getString("$key.template") ?: return
            val addition = smithRecipeConf.getString("$key.addition") ?: return

            val parts = result.split("/")
            val amount = parts.getOrNull(1)?.toIntOrNull() ?: 1

            SmithRecipe.builder(
                key,
                getInput(template) ?: return,
                getInput(base) ?: return,
                getInput(addition) ?: return,
                getMaterial(parts[0], amount) ?: return
            ).add()
        }
    }

    fun configRecipes(config: FoxConfig = recipeConf, namespace: String = "plushies") {
        for (key in config.getKeys()) {
            val shape = config.getStringList("$key.shape")
            val result = config.getString("$key.result") ?: continue
            val parts = result.split("/")
            val amount = parts.getOrNull(1)?.toIntOrNull() ?: 1

            if (shape.isEmpty()) {
                // shapeless recipe
                val output = getMaterial(parts[0], amount) ?: continue // skip if output is invalid
                CraftRecipe.builder(key, output)
                    .apply {
                        for (ingredient in config.getStringList("$key.ingredients")) {
                            val ingparts = ingredient.split("/")
                            val ingamount = ingparts.getOrNull(1)?.toIntOrNull() ?: 1

                            val material = getInput(ingparts[0]) ?: continue // skip if input is invalid

                            // nice!, add ingredient to recipe
                            ingredient(material, ingamount)
                        }
                    }.shapeless(namespace)
            } else {

                if (shape.size != 3)  {
                    logger("Plushies").warn("Invalid shape for recipe $key in '$namespace'")
                    continue // skip if shape is invalid
                }

                val output = getMaterial(parts[0], amount) ?: continue
                CraftRecipe.builder(key, output)
                    .shape(shape[0], shape[1], shape[2])
                    .apply {
                        for (ingredient in config.getKeys("$key.ingredients")) {
                            val material = config.getString("$key.ingredients.$ingredient")
                                ?.let { getInput(it) } ?: continue // skip if input is invalid

                            // all valid, add ingredient
                            ingredient(ingredient[0], material)
                        }
                    }.shaped(namespace)
            }
        }
    }
}
