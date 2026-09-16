package co.akoot.plugins.plushies.util

import co.akoot.plugins.plushies.util.builders.CraftRecipe
import io.papermc.paper.datacomponent.DataComponentTypes
import io.papermc.paper.datacomponent.item.ItemContainerContents
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.RecipeChoice

// epic asF
fun shulkerBox(contents: List<ItemStack>, amount: Int = 1, mat: Material = Material.SHULKER_BOX): ItemStack {
    val box = ItemStack(mat,amount)
    box.setData(DataComponentTypes.CONTAINER, ItemContainerContents.containerContents(contents))
    return box
}

fun fullBoxMatches(stack: ItemStack, material: Material): Boolean {
    val contents = stack.getData(DataComponentTypes.CONTAINER) ?: return false

    return contents.contents().all {
        it.type == material && it.amount == 64
    }
}

fun fullBoxOf(vararg materials: Material): RecipeChoice {
    return RecipeChoice.predicateChoice(
        { stack ->
            materials.any { fullBoxMatches(stack, it) }
        },
        shulkerBox(
            List(27) { ItemStack(materials.first(), 64) }
        )
    )
}

fun shulkers() {
    // raymond
    CraftRecipe.builder(
        "tntbox", shulkerBox(List(27) { ItemStack(Material.TNT, 64) }, mat = Material.RED_SHULKER_BOX))
        .ingredient('g', fullBoxOf(Material.GUNPOWDER))
        .ingredient('s', fullBoxOf(Material.SAND, Material.RED_SAND))
        .shape("gsg", "sgs", "gsg")
        .shaped()
}