package co.akoot.plugins.plushies.gui

import co.akoot.plugins.bluefox.util.ColorUtil.randomColor
import co.akoot.plugins.bluefox.util.Txt
import co.akoot.plugins.plushies.gui.MenuItems.prevPage
import co.akoot.plugins.plushies.gui.MenuItems.nextPage
import co.akoot.plugins.plushies.util.builders.ChestGUI
import co.akoot.plugins.plushies.util.builders.ItemBuilder
import org.bukkit.Material
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack
import kotlin.math.min

class BookMenu(private val page: Int = 1) : InventoryHolder {

    private val bookMenu: Inventory = ChestGUI.builder(45, this, true).apply {
        title(Txt("Books").color(randomColor(brightness = 0.6f)).c)
        if (page == 1) setItem(44, nextPage) else setItem(36, prevPage)
        setItems(0..35, setBooks(page))
    }.build()

    fun nextPage(): BookMenu {
        return BookMenu(page + 1)
    }

    fun prevPage(): BookMenu {
        return BookMenu(page - 1)
    }

    private fun setBooks(pageNumber: Int): List<ItemStack> {
        val bookList = mutableListOf<ItemStack>()

        val start = (pageNumber - 1) * 36
        val end = min(start + 36, 72)

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