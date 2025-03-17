package co.akoot.plugins.plushies.listeners

import co.akoot.plugins.plushies.gui.BookArchiveMenu
import co.akoot.plugins.plushies.gui.BookArchiveMenu.Companion.bookArchiveMenu
import co.akoot.plugins.plushies.gui.BookMenu
import co.akoot.plugins.plushies.gui.BookMenu.Companion.bookMenu
import co.akoot.plugins.plushies.gui.PlushieMainMenu
import co.akoot.plugins.plushies.gui.PlushieMainMenu.Companion.mainMenu
import co.akoot.plugins.plushies.gui.PlushieMenu
import co.akoot.plugins.plushies.gui.PlushieMenu.Companion.plushMenu
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent

class GUI : Listener {
    @EventHandler
    fun onInvClick(event: InventoryClickEvent) {
        val menuItem = event.currentItem ?: return
        val p = event.whoClicked

        when (val holder = event.clickedInventory?.holder) {
            is PlushieMainMenu -> {
                mainMenu(menuItem, p)
                event.isCancelled = true
            }

            is PlushieMenu -> {
                plushMenu(menuItem, p, holder)
                event.isCancelled = true
            }

            is BookMenu -> {
                bookMenu(menuItem, p, holder)
                event.isCancelled = true
            }

            is BookArchiveMenu -> {
                bookArchiveMenu(menuItem, p, holder)
                event.isCancelled = true
            }

            else -> return
        }
    }
//    @EventHandler
//    fun onInvClose(event: InventoryCloseEvent) {
//        val inv = event.inventory
//        val menu = inv.getHolder(false)
//        val p = event.player
//        when (menu) {
//            is PlushieMainMenu -> {
//                p.sendMessage(Txt("Test").color("error_accent").c)
//            }
//        }
//    }
}