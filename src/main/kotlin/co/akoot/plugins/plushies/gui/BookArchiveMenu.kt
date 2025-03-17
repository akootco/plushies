package co.akoot.plugins.plushies.gui

import co.akoot.plugins.bluefox.api.FoxPlugin
import co.akoot.plugins.bluefox.util.ColorUtil.randomColor
import co.akoot.plugins.bluefox.util.Text
import co.akoot.plugins.plushies.gui.MenuItems.prevPage
import co.akoot.plugins.plushies.gui.MenuItems.nextPage
import co.akoot.plugins.plushies.util.BookArchiver
import co.akoot.plugins.plushies.util.builders.ChestGUI
import org.bukkit.Material
import org.bukkit.entity.HumanEntity
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.BookMeta
import java.io.File
import kotlin.math.min

class BookArchiveMenu(private val plugin: FoxPlugin, private val page: Int = 1) : InventoryHolder {

    companion object {
        fun bookArchiveMenu(item: ItemStack, p: HumanEntity, holder: InventoryHolder) {
            when (item) {
                nextPage -> p.openInventory((holder as BookArchiveMenu).nextPage().inventory)
                prevPage -> p.openInventory((holder as BookArchiveMenu).prevPage().inventory)
            }

            if (item.type == Material.WRITTEN_BOOK) {
                p.openBook(item.itemMeta as BookMeta)
            }
        }
    }

    private val bookList = mutableListOf<ItemStack>()

    private val bookMenu: Inventory = ChestGUI.builder(54, this, true).apply {
        title(Text("Book Archive").color(randomColor(brightness = 0.6f)).component)
        if (page > 1) setItem(45, prevPage)
        setItems(0..44, setBooks(page))
        if (bookList.size > page * 45) setItem(53, nextPage)
    }.build()

    fun nextPage(): BookArchiveMenu {
        return BookArchiveMenu(plugin, page + 1)
    }

    fun prevPage(): BookArchiveMenu {
        return BookArchiveMenu(plugin, page - 1)
    }

    private fun setBooks(pageNumber: Int): List<ItemStack> {
        val files = File(plugin.dataFolder, "books").listFiles() ?: return bookList

        val start = (pageNumber - 1) * 45
        val end = min(start + 45, files.size)

        for (i in start until end) {
            // set all books that are saved
            BookArchiver(plugin).loadBook(files[i].nameWithoutExtension)?.let { bookList.add(it) }
        }

        return bookList
    }

    override fun getInventory(): Inventory {
        return this.bookMenu
    }
}