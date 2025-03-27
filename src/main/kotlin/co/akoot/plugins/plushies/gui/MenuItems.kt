package co.akoot.plugins.plushies.gui

import co.akoot.plugins.bluefox.util.Text
import co.akoot.plugins.plushies.util.builders.ItemBuilder
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

object MenuItems {

    val home = ItemBuilder.builder(ItemStack(Material.PURPLE_BED))
        .itemName(Text("Home", NamedTextColor.GREEN).component).build()

    val nextPage = ItemBuilder.builder(ItemStack(Material.PAPER))
        .itemName(Text("→", NamedTextColor.GOLD).component).build()

    val prevPage = ItemBuilder.builder(ItemStack(Material.PAPER))
        .itemName(Text("←", NamedTextColor.GOLD).component).build()

    // plushie menu nav button
    val pMenu = ItemBuilder.builder(ItemStack(Material.TOTEM_OF_UNDYING))
        .itemName(Text("Plushies", NamedTextColor.GREEN).component).build()

    // statue menu nav button
    val sMenu = ItemBuilder.builder(ItemStack(Material.ARMOR_STAND))
        .itemName(Text("Statues", NamedTextColor.GREEN).component).build()

    val filler = ItemBuilder.builder(ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE))
        .filler().build()
}