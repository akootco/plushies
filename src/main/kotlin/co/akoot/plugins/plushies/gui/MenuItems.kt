package co.akoot.plugins.plushies.gui

import co.akoot.plugins.bluefox.util.Text
import co.akoot.plugins.plushies.util.builders.ItemBuilder
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

object MenuItems {

    val nextPage = ItemBuilder.builder(ItemStack(Material.PAPER))
        .itemName(Text("→", NamedTextColor.GOLD).component).build()

    val prevPage = ItemBuilder.builder(ItemStack(Material.PAPER))
        .itemName(Text("←", NamedTextColor.GOLD).component).build()

    val filler = ItemBuilder.builder(ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE))
        .filler().build()
}