package co.akoot.plugins.plushies.gui

import co.akoot.plugins.bluefox.util.ColorUtil.randomColor
import co.akoot.plugins.bluefox.util.Text
import co.akoot.plugins.plushies.gui.MenuItems.home
import co.akoot.plugins.plushies.gui.MenuItems.nextPage
import co.akoot.plugins.plushies.util.Plush.plushies
import co.akoot.plugins.plushies.gui.MenuItems.prevPage
import co.akoot.plugins.plushies.util.Plush.createPlushie
import co.akoot.plugins.plushies.util.builders.ChestGUI
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack
import kotlin.math.min

class PlushieMenu(private val isStatue: Boolean = false, private val page: Int = 1, bedrock: Boolean = false) :
    InventoryHolder {

    private val plushMenu: Inventory = ChestGUI.builder(54, this, true).apply {
        title(Text(if (isStatue) "Statues" else "Plushies").color(randomColor(brightness = 0.6f)).component)
        if (page > 1) setItem(45, prevPage)
        if (!bedrock) setItem(49, home)
        if (plushies.size > page * 45) setItem(53, nextPage)
        setItems(0..44, setPlushies(page))
    }.build()

    // create list of plushie items
    private fun setPlushies(pageNumber: Int): List<ItemStack> {
        val plushList = mutableListOf<ItemStack>()

        val start = (pageNumber - 1) * 45
        val end = min(start + 45, plushies.size)

        val sortedPlushies = plushies.sortedBy { it.first }
        // only get what fits on the page
        for (index in start until end) {
            val plushie = sortedPlushies[index]
            val name = plushie.first.replace("_.*".toRegex(), "")
            plushList.add(createPlushie(name, plushie.second))
        }

        return plushList
    }

    fun nextPage(bedrock: Boolean = false): PlushieMenu {
        return PlushieMenu(isStatue, page + 1, bedrock)
    }

    fun prevPage(bedrock: Boolean = false): PlushieMenu {
        return PlushieMenu(isStatue, page - 1, bedrock)
    }

    fun isStatue(): Boolean {
        return isStatue
    }

    override fun getInventory(): Inventory {
        return this.plushMenu
    }
}