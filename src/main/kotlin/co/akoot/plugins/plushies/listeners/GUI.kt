package co.akoot.plugins.plushies.listeners

import co.akoot.plugins.bluefox.util.runLater
import co.akoot.plugins.plushies.gui.BookArchiveMenu
import co.akoot.plugins.plushies.gui.BookArchiveMenu.Companion.bookArchiveMenu
import co.akoot.plugins.plushies.gui.BookMenu
import co.akoot.plugins.plushies.gui.BookMenu.Companion.bookMenu
import co.akoot.plugins.plushies.gui.CustomItemMenu
import co.akoot.plugins.plushies.gui.CustomItemMenu.Companion.customItemMenu
import co.akoot.plugins.plushies.gui.PlushieMenu
import co.akoot.plugins.plushies.gui.PlushieMenu.Companion.plushMenu
import co.akoot.plugins.plushies.gui.atm.ATMMenu
import co.akoot.plugins.plushies.gui.atm.ATMMenu.Companion.atmMainMenu
import co.akoot.plugins.plushies.gui.atm.CoinMenu
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryAction
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryDragEvent

class GUI : Listener {
    @EventHandler
    fun onInvClick(event: InventoryClickEvent) {
        val menuItem = event.currentItem ?: return
        val p = event.whoClicked as Player //???????? excuse me?

        when (val holder = event.inventory.holder) {
            is PlushieMenu -> {
                plushMenu(menuItem, p, holder, event.click)
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

            is CustomItemMenu -> {
                customItemMenu(menuItem, p, holder)
                event.isCancelled = true
            }

            is ATMMenu -> {
                atmMainMenu(menuItem, p)
                event.isCancelled = true
            }

            is CoinMenu -> {
                event.isCancelled = true
                // this doesn't work yet!
//                val allowed = listOf(holder.coin.backing, holder.coin.backingBlock)
//
//                if (event.cursor.type !in allowed || event.currentItem?.type !in allowed) {
//                    event.isCancelled = true
//                }
            }

            else -> return
        }
    }

    @EventHandler
    fun onInventoryDrag(event: InventoryDragEvent) {
        when (val holder = event.inventory.holder) {
            is CoinMenu -> {
                event.isCancelled = true
//                for ((_, item) in event.newItems) {
//                    if (item.type !in listOf(holder.coin.backing, holder.coin.backingBlock))
//                        event.isCancelled = true
//                }
            }
            else -> return
        }
    }

    @EventHandler
    fun onInvClose(event: InventoryCloseEvent) {
        val p = event.player as Player
        when (event.inventory.holder) {
            is CoinMenu -> {
                // 1 tick delay or the server crashes, pretty cool
                runLater(1) { p.openInventory(ATMMenu(p).inventory) }
            }
        }
    }
}