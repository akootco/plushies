package co.akoot.plugins.plushies.api

import co.akoot.plugins.bluefox.extensions.getPDC
import co.akoot.plugins.bluefox.extensions.setPDC
import co.akoot.plugins.bluefox.util.runLater
import co.akoot.plugins.plushies.Plushies.Companion.key
import co.akoot.plugins.plushies.util.builders.ItemBuilder
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

fun Player.openMenu(menu: Storage) {
    openInventory(menu.getInventory())

    menu.openSound?.let {
        playSound(location, it.sound, it.volume,it.pitch)
    }
}

abstract class Storage(
    title: Component,
    private val key: String,
    private val config: StorageConfig = StorageConfig(),
    private val save: Boolean = true
) : Menu {

    data class StorageConfig(
        val inventoryType: InventoryType = InventoryType.CHEST,
        val rows: Int = 1,
        val openSlots: Set<Int>? = null
    )

    data class StorageSound(
        val sound: String,
        val pitch: Float = 1f,
        val volume: Float = 1f,
    )

    protected abstract val entity: Entity

    open val openSound: StorageSound?
        get() = null

    open val closeSound: StorageSound?
        get() = null

    private val inventory = if (config.inventoryType == InventoryType.CHEST) {
        Bukkit.createInventory(
            this,
            config.rows.coerceIn(1, 6) * 9,
            title
        )
    } else {
        Bukkit.createInventory(
            this,
            config.inventoryType,
            title
        )
    }

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
        event.isCancelled = config.openSlots?.let { event.rawSlot !in it } ?: false
    }

    override fun onClose(event: InventoryCloseEvent) {
        closeSound?.let {
            event.player.world.playSound(event.player.location, it.sound, it.volume,it.pitch)
        }
        if (save) {
            runLater {
                if (inventory.viewers.isNotEmpty()) return@runLater

                entity.setPDC(
                    key(key),
                    ItemStack.serializeItemsAsBytes(inventory.contents)
                )
            }
        }
    }

    fun loadContents() {
        entity.getPDC<ByteArray>(key(key))?.let {
            inventory.contents = ItemStack.deserializeItemsFromBytes(it)
        }

        config.openSlots?.let { slots ->
            for (slot in 0 until inventory.size) {
                if (slot !in slots) {
                    inventory.setItem(slot, FILLER)
                }
            }
        }
    }

    fun remove() {
        inventory.viewers.toList().forEach { player ->
            player.closeInventory()
        }

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