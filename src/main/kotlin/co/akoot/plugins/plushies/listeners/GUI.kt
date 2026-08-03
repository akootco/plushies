package co.akoot.plugins.plushies.listeners

import co.akoot.plugins.plushies.api.ChestMenu
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent

class GUI : Listener {
    @EventHandler
    fun onInvClick(event: InventoryClickEvent) {
        val menu = event.inventory.holder as? ChestMenu ?: return

        if (event.clickedInventory != event.view.topInventory) return

        event.isCancelled = true
        menu.onClick(event)
    }
}