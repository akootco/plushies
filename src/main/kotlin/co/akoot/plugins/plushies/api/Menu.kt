package co.akoot.plugins.plushies.api

import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.InventoryHolder

interface Menu : InventoryHolder {
    val ignoreBottom: Boolean
        get() = true
    fun onClick(event: InventoryClickEvent)
    fun onClose(event: InventoryCloseEvent) {}
}