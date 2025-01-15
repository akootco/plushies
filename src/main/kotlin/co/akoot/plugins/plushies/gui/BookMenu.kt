package co.akoot.plugins.plushies.gui

import co.akoot.plugins.bluefox.util.ColorUtil.randomColor
import co.akoot.plugins.bluefox.util.Txt
import co.akoot.plugins.plushies.gui.Plush.prevPage
import co.akoot.plugins.plushies.gui.Plush.nextPage
import co.akoot.plugins.plushies.util.builders.ChestGUI
import co.akoot.plugins.plushies.util.builders.ItemBuilder
import org.bukkit.Material
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack
import kotlin.math.min

class BookMenu(private val page: Int = 1) : InventoryHolder {

    private val bookMenu: Inventory = ChestGUI.builder(54, this, true)
        .title(Txt("Books").color(randomColor(brightness = 0.6f)).c)
        .setItem(45, prevPage)
        .setItem(53, nextPage)
        .setItems(0..44, setBooks(page))
        .build()

    fun nextPage(): BookMenu {
        return BookMenu(page + 1)
    }

    fun prevPage(): BookMenu {
        return BookMenu(if (page > 1) page - 1 else 1)
    }

    fun setBooks(pageNumber: Int): List<ItemStack> {
        val bookList = mutableListOf<ItemStack>()

        val start = (pageNumber - 1) * 45
        val end = min(start + 45, 72)

        // only get what fits on the page
        for (book in start until end) {
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