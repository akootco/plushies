package co.akoot.plugins.plushies.gui

import co.akoot.plugins.bluefox.util.ColorUtil.randomColor
import co.akoot.plugins.bluefox.util.Txt
import co.akoot.plugins.plushies.util.builders.ChestGUI
import co.akoot.plugins.plushies.util.builders.ItemBuilder
import org.bukkit.Material
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack

class BookMenu() : InventoryHolder {

    private val range = 0..26

    private val bookMenu: Inventory = ChestGUI.builder(27, this, true)
        .title(Txt("Books").color(randomColor(brightness = 0.6f)).c)
        .setItems(range, setBooks())
        .build()

    // TODO: add book covers to resource pack
    private fun setBooks(): List<ItemStack> {
        val bookList = mutableListOf<ItemStack>()
        // add books to menu
        for (book in range) {
            bookList.add(ItemBuilder.builder(ItemStack(Material.WRITTEN_BOOK))
                .customModelData(book)
                .build())
        }

        return bookList
    }

    override fun getInventory(): Inventory {
        return this.bookMenu
    }
}