package co.akoot.plugins.plushies.gui

import co.akoot.plugins.bluefox.util.ColorUtil.randomColor
import co.akoot.plugins.bluefox.util.Txt
import co.akoot.plugins.plushies.gui.Plush.home
import co.akoot.plugins.plushies.gui.Plush.nextPage
import co.akoot.plugins.plushies.gui.Plush.pMenu
import co.akoot.plugins.plushies.gui.Plush.prevPage
import co.akoot.plugins.plushies.gui.Plush.sMenu
import co.akoot.plugins.plushies.gui.Plush.setPlushies
import co.akoot.plugins.plushies.util.builders.ChestGUI
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder

class PlushieMenu(private val isStatue: Boolean = false, private val page: Int = 1) : InventoryHolder {

    private val mainMenu: Inventory = ChestGUI.builder(9,this, true)
        .title((Txt("Plushies").color(randomColor(brightness = 0.6f))).c)
        .setItem(2, pMenu)
        .setItem(6, sMenu)
        .build()

    private val plushMenu: Inventory = ChestGUI.builder(54, this, true)
        .title(Txt(if (isStatue) "Statues" else "Plushies").color(randomColor(brightness = 0.6f)).c)
        .setItem(45,prevPage)
        .setItem(49, home)
        .setItem(53, nextPage)
        .setItems(0..44, setPlushies(page))
        .build()

    override fun getInventory(): Inventory {
        return this.plushMenu
    }

    fun mainMenu(): Inventory {
        return this.mainMenu
    }

    fun nextPage(): PlushieMenu {
        return PlushieMenu(isStatue, page + 1)
    }

    fun isStatue(): Boolean {
        return isStatue
    }

    fun prevPage(): PlushieMenu {
        return PlushieMenu(isStatue, if (page > 1) page - 1 else 1)
    }
}