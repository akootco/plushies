package co.akoot.plugins.plushies.util

import co.akoot.plugins.plushies.util.builders.CraftRecipe
import io.papermc.paper.datacomponent.DataComponentTypes
import io.papermc.paper.datacomponent.item.ItemContainerContents
import org.bukkit.Material
import org.bukkit.Tag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.RecipeChoice.ExactChoice

// epic asF
fun shulkerBox(contents: List<ItemStack>, amount: Int = 1, mat: Material = Material.SHULKER_BOX): ItemStack {
    val box = ItemStack(mat,amount)
    box.setData(DataComponentTypes.CONTAINER, ItemContainerContents.containerContents(contents))
    return box
}

fun fullBoxOf(material: Material): List<ItemStack> {
    return Tag.SHULKER_BOXES.values.map { color ->
        shulkerBox(List(27) { ItemStack(material, 64) }, 1, color)
    }
}

fun shulkers() {
    // raymond
    CraftRecipe.builder(
        "tntbox", shulkerBox(List(27) { ItemStack(Material.TNT, 64) }, mat = Material.RED_SHULKER_BOX))
        .ingredient('g', ExactChoice(fullBoxOf(Material.GUNPOWDER)))
        .ingredient('s', ExactChoice(fullBoxOf(Material.SAND) + fullBoxOf(Material.RED_SAND)))
        .shape("gsg", "sgs", "gsg")
        .shaped()
}