package co.akoot.plugins.plushies.listeners

import co.akoot.plugins.plushies.gui.BookArchiveMenu
import co.akoot.plugins.plushies.gui.BookArchiveMenu.Companion.bookArchiveMenu
import co.akoot.plugins.plushies.gui.BookMenu
import co.akoot.plugins.plushies.gui.BookMenu.Companion.bookMenu
import co.akoot.plugins.plushies.gui.CustomItemMenu
import co.akoot.plugins.plushies.gui.CustomItemMenu.Companion.customItemMenu
import co.akoot.plugins.plushies.gui.PlushieMenu
import co.akoot.plugins.plushies.gui.PlushieMenu.Companion.plushMenu
import co.akoot.plugins.plushies.gui.SellIHeadMenu
import co.akoot.plugins.plushies.gui.atm.ATMMenu
import co.akoot.plugins.plushies.gui.atm.ATMMenu.Companion.atmMainMenu
import co.akoot.plugins.plushies.gui.atm.CoinMenu
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent

class GUI : Listener {
    @EventHandler
    fun onInvClick(event: InventoryClickEvent) {
        val menuItem = event.currentItem ?: return
        val p = event.whoClicked as Player //???????? excuse me?

        when (val holder = event.inventory.holder) {
            is PlushieMenu -> {
                if (event.clickedInventory != event.view.topInventory) return
                plushMenu(menuItem, p, holder, event.click)
                event.isCancelled = true
            }

            is BookMenu -> {
                if (event.clickedInventory != event.view.topInventory) return
                bookMenu(menuItem, p, holder)
                event.isCancelled = true
            }

            is BookArchiveMenu -> {
                if (event.clickedInventory != event.view.topInventory) return
                bookArchiveMenu(menuItem, p, holder)
                event.isCancelled = true
            }

            is CustomItemMenu -> {
                if (event.clickedInventory != event.view.topInventory) return
                customItemMenu(menuItem, p, holder)
                event.isCancelled = true
            }

            is ATMMenu -> {
                atmMainMenu(menuItem, p)
                event.isCancelled = true
            }

            is CoinMenu -> CoinMenu.onClick(holder, event)
            is SellIHeadMenu -> SellIHeadMenu.onClick(p, event)

            else -> return
        }
    }

    @EventHandler
    fun onInvClose(event: InventoryCloseEvent) {
        val p = event.player as Player
        when (val holder = event.inventory.holder) {
            is CoinMenu -> CoinMenu.onClose(holder, p, event)
            is SellIHeadMenu -> SellIHeadMenu.onClose(p, event)
        }
    }
}