package co.akoot.plugins.plushies.gui

import co.akoot.plugins.bluefox.util.ColorUtil.randomColor
import co.akoot.plugins.bluefox.util.Text
import co.akoot.plugins.plushies.gui.MenuItems.pMenu
import co.akoot.plugins.plushies.gui.MenuItems.sMenu
import co.akoot.plugins.plushies.util.builders.ChestGUI
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder

class PlushieMainMenu : InventoryHolder {

    private val mainMenu: Inventory = ChestGUI.builder(9,this, true)
        .title((Text("Plushies").color(randomColor(brightness = 0.6f))).component)
        .setItem(2, pMenu)
        .setItem(6, sMenu)
        .build()

    override fun getInventory(): Inventory {
        return this.mainMenu
    }
}