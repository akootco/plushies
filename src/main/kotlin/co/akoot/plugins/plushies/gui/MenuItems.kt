package co.akoot.plugins.plushies.gui

import co.akoot.plugins.bluefox.util.Txt
import co.akoot.plugins.plushies.util.builders.ItemBuilder
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

object MenuItems {

    val home = ItemBuilder.builder(ItemStack(Material.PURPLE_BED))
        .itemName(Txt("Home").color(NamedTextColor.GREEN).c).build()

    val nextPage = ItemBuilder.builder(ItemStack(Material.PAPER))
        .itemName(Txt("→").color(NamedTextColor.GOLD).c).build()

    val prevPage = ItemBuilder.builder(ItemStack(Material.PAPER))
        .itemName(Txt("←").color(NamedTextColor.GOLD).c).build()

    // plushie menu nav button
    val pMenu = ItemBuilder.builder(ItemStack(Material.TOTEM_OF_UNDYING))
        .itemName(Txt("Plushies").color(NamedTextColor.GREEN).c).build()

    // statue menu nav button
    val sMenu = ItemBuilder.builder(ItemStack(Material.ARMOR_STAND))
        .itemName(Txt("Statues").color(NamedTextColor.GREEN).c).build()
}