package co.akoot.plugins.plushies.listeners

import co.akoot.plugins.plushies.api.Menu
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent

class GUI : Listener {
    @EventHandler
    fun onInvClick(event: InventoryClickEvent) {
        val menu = event.view.topInventory.holder as? Menu ?: return
        if (event.clickedInventory != event.view.topInventory) return
        menu.onClick(event)
    }

    @EventHandler
    fun onInvClose(event: InventoryCloseEvent) {
        val menu = event.inventory.holder as? Menu ?: return
        menu.onClose(event)
    }
}