package co.akoot.plugins.plushies.gui

import co.akoot.plugins.bluefox.api.Kolor
import co.akoot.plugins.bluefox.extensions.invoke
import co.akoot.plugins.bluefox.util.ColorUtil.randomColor
import co.akoot.plugins.bluefox.util.Text
import co.akoot.plugins.plushies.gui.MenuItems.nextPage
import co.akoot.plugins.plushies.util.Items.plushies
import co.akoot.plugins.plushies.gui.MenuItems.prevPage
import co.akoot.plugins.plushies.util.Items.createPlushie
import co.akoot.plugins.plushies.util.Util.plushMsg
import co.akoot.plugins.plushies.util.builders.ChestGUI
import co.akoot.plugins.plushies.util.builders.ItemBuilder
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack
import kotlin.math.min

class PlushieMenu(private val page: Int = 1) :
    InventoryHolder {

    companion object {
        fun plushMenu(item: ItemStack, p: Player, holder: InventoryHolder, clickType: ClickType) {

            when (item) {
                nextPage -> p.openInventory((holder as PlushieMenu).nextPage().inventory)
                prevPage -> p.openInventory((holder as PlushieMenu).prevPage().inventory)
            }

            val pItem = p.inventory.itemInMainHand

            if (item.type == Material.TOTEM_OF_UNDYING) { // is it a friend?

                if (pItem.type != Material.TOTEM_OF_UNDYING) { // HEY! no hacking!
                    Kolor.ERROR.accent("You must be holding a totem!").send(p)
                } else {
                    when (clickType) {
                        ClickType.RIGHT -> ItemBuilder.builder(pItem).copyOf(item)
                            .customModelData(pItem.itemMeta.customModelData + 1).build() // statue

                        else -> ItemBuilder.builder(pItem).copyOf(item).build() // normal
                    }

                    plushMsg(PlainTextComponentSerializer.plainText().serialize(item.effectiveName())).component
                }
            }
        }
    }

    private val plushMenu: Inventory = ChestGUI.builder(54, this, true).apply {
        title(Text("Plushies").color(randomColor(brightness = 0.6f)).component)
        if (page > 1) setItem(45, prevPage)
        if (plushies.size > page * 45) setItem(53, nextPage)
        setItems(0..44, setPlushies(page))
    }.build()

    // create list of plushie items
    private fun setPlushies(pageNumber: Int): List<ItemStack> {
        val plushList = mutableListOf<ItemStack>()

        val start = (pageNumber - 1) * 45
        val end = min(start + 45, plushies.size)

        val sortedPlushies = plushies.sortedBy { it.first.lowercase() }
        // only get what fits on the page
        for (index in start until end) {
            val plushie = sortedPlushies[index]
            val name = plushie.first.replace("_.*".toRegex(), "")
            plushList.add(createPlushie(name, plushie.second))
        }

        return plushList
    }

    fun nextPage(): PlushieMenu {
        return PlushieMenu(page + 1)
    }

    fun prevPage(): PlushieMenu {
        return PlushieMenu(page - 1)
    }

    override fun getInventory(): Inventory {
        return this.plushMenu
    }
}