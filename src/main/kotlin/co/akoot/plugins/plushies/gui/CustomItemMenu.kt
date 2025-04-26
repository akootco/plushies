package co.akoot.plugins.plushies.gui

import co.akoot.plugins.bluefox.util.ColorUtil.randomColor
import co.akoot.plugins.bluefox.util.Text
import co.akoot.plugins.plushies.gui.MenuItems.filler
import co.akoot.plugins.plushies.gui.MenuItems.nextPage
import co.akoot.plugins.plushies.gui.MenuItems.prevPage
import co.akoot.plugins.plushies.util.Items.customItems
import co.akoot.plugins.plushies.util.Items.isCustomItem
import co.akoot.plugins.plushies.util.builders.ChestGUI
import org.bukkit.entity.HumanEntity
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack
import kotlin.math.min

class CustomItemMenu(private val page: Int = 1) : InventoryHolder {

    companion object {
        fun customItemMenu(item: ItemStack, p: HumanEntity, holder: InventoryHolder) {
            when (item) {
                filler -> return
                nextPage -> {
                    p.openInventory((holder as CustomItemMenu).nextPage().inventory)
                    return
                }
                prevPage -> {
                    p.openInventory((holder as CustomItemMenu).prevPage().inventory)
                    return
                }
            }
            // if not item above, give it to player
            p.inventory.addItem(item)
        }
    }

    val items = customItems.values.filter { it.isCustomItem }

    private val itemMenu: Inventory = ChestGUI.builder(54, this, true).apply {
        title(Text("Custom Items").color(randomColor(brightness = 0.6f)).component)
        if (page > 1) setItem(45, prevPage)
        if (items.size > page * 45) setItem(53, nextPage)
        setItems(0..44, getItems(page))
    }.build()

    // create list of plushie items
    private fun getItems(pageNumber: Int): List<ItemStack> {
        val itemList = mutableListOf<ItemStack>()

        val start = (pageNumber - 1) * 45
        val end = min(start + 45, items.size)

        // only get what fits on the page
        for (index in start until end) {
            itemList.add(items.toList()[index])
        }

        return itemList
    }

    fun nextPage(): CustomItemMenu {
        return CustomItemMenu(page + 1)
    }

    fun prevPage(): CustomItemMenu {
        return CustomItemMenu(page - 1)
    }

    override fun getInventory(): Inventory {
        return this.itemMenu
    }
}