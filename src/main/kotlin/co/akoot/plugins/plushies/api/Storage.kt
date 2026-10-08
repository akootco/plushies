package co.akoot.plugins.plushies.api

import co.akoot.plugins.bluefox.extensions.getPDC
import co.akoot.plugins.bluefox.extensions.setPDC
import co.akoot.plugins.plushies.Plushies.Companion.key
import co.akoot.plugins.plushies.util.builders.ItemBuilder
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Entity
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

abstract class Storage(
    title: Component,
    private val key: String,
    rows: Int = 1,
    private val openSlots: Set<Int>? = null
) : Menu {

    protected abstract val entity: Entity

    private val inventory =
        Bukkit.createInventory(this, rows.coerceIn(1, 6) * 9, title)

    companion object {
        private val FILLER =
            ItemBuilder.builder(
                ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE)
            )
                .itemName(Component.empty())
                .itemModel("slot")
                .hideTooltip()
                .build()
    }

    override fun getInventory(): Inventory = inventory

    override fun onClick(event: InventoryClickEvent) {
        event.isCancelled = openSlots?.let { event.rawSlot !in it } ?: false
    }


    override fun onClose(event: InventoryCloseEvent) {
        if (inventory.viewers.isNotEmpty()) return

        entity.setPDC(
            key(key),
            ItemStack.serializeItemsAsBytes(inventory.contents)
        )
    }

    fun loadContents() {
        entity.getPDC<ByteArray>(key(key))?.let {
            inventory.contents = ItemStack.deserializeItemsFromBytes(it)
        }

        openSlots?.let { slots ->
            for (slot in 0 until inventory.size) {
                if (slot !in slots) {
                    inventory.setItem(slot, FILLER)
                }
            }
        }
    }

    fun remove() {
        inventory.viewers.forEach { it.closeInventory() }

        inventory.contents
            .filterNotNull()
            .filterNot { it.isSimilar(FILLER) }
            .forEach { item ->
                entity.world.dropItemNaturally(
                    entity.location.clone().add(0.0, 0.5, 0.0),
                    item
                )
            }
    }
}