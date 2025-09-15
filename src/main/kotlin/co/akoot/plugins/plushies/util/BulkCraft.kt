package co.akoot.plugins.plushies.util

import co.akoot.plugins.plushies.util.builders.CraftRecipe
import io.papermc.paper.datacomponent.DataComponentTypes
import io.papermc.paper.datacomponent.item.Fireworks
import io.papermc.paper.datacomponent.item.ItemContainerContents
import org.bukkit.Material
import org.bukkit.Tag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.RecipeChoice.ExactChoice

// epic asF
private fun shulkerBox(contents: List<ItemStack>, amount: Int = 1, mat: Material = Material.SHULKER_BOX): ItemStack {
    val box = ItemStack(mat,amount)
    box.setData(DataComponentTypes.CONTAINER, ItemContainerContents.containerContents(contents))
    return box
}

private fun fireworkStack(dur: Int): ItemStack {
    val firework = ItemStack(Material.FIREWORK_ROCKET, 64)
    firework.setData(DataComponentTypes.FIREWORKS, Fireworks.fireworks(listOf(), dur))
    return firework
}

private fun fullBoxOf(material: Material): List<ItemStack> {
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

    // fireworks
    for (dur in 1..3) {
        val color = mapOf(
            1 to Material.LIME_SHULKER_BOX,
            2 to Material.YELLOW_SHULKER_BOX,
            3 to Material.RED_SHULKER_BOX
        )

        CraftRecipe.builder(
            "fireworkbox.$dur",
            shulkerBox(List(27) { fireworkStack(dur) },
                3, // good tease, stack separates itself lol
                color[dur] ?: Material.SHULKER_BOX // my good pal alvin operation .let?.run { it.takeif.sortedBy it!= e(erm)!! }
            )
        )
            .ingredient(ExactChoice(fullBoxOf(Material.GUNPOWDER)), dur)
            .ingredient(ExactChoice(fullBoxOf(Material.PAPER) + fullBoxOf(Material.SUGAR_CANE)))
            .shapeless()
    }
}