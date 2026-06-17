package co.akoot.plugins.plushies.util

import co.akoot.plugins.bluefox.util.Text
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
        CraftRecipe.builder(
            "everlastingfirework.$dur",
            ItemStack(Material.FIREWORK_ROCKET).makeEverlastingRocket(dur)
        )
            .ingredient(ExactChoice(fullBoxOf(Material.GUNPOWDER)), dur)
            .ingredient(ExactChoice(fullBoxOf(Material.PAPER) + fullBoxOf(Material.SUGAR_CANE)))
            .shapeless()
    }
}

fun ItemStack.consumeEverLastRocket(): Boolean {
    val maxDamage = getData(DataComponentTypes.MAX_DAMAGE) ?: return false
    val damage = getData(DataComponentTypes.DAMAGE) ?: 0
    if (damage >= maxDamage - 1) return false

    setData(DataComponentTypes.DAMAGE, damage + 1)
    return true
}

fun ItemStack.makeEverlastingRocket(duration: Int) = apply {
    setData(DataComponentTypes.ITEM_NAME, Text("Everlasting Rocket").component)
    setData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true)
    setData(DataComponentTypes.MAX_STACK_SIZE, 1)
    setData(DataComponentTypes.MAX_DAMAGE, 5184)
    setData(
        DataComponentTypes.FIREWORKS,
        Fireworks.fireworks().flightDuration(duration)
    )
}