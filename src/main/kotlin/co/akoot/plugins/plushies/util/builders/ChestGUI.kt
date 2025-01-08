package co.akoot.plugins.plushies.util.builders

import co.akoot.plugins.bluefox.util.Txt
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack

// TODO: info sucks. make it better, or else!!!!!!!!!!!!!!!

/**
 * Chest GUI
 *
 * @property size The size of the inventory (must be between 9 and 54, and a multiple of 9). Defaults to 27 if invalid.
 * @property holder The holder of the inventory, or null if none.
 * @property isMenu If true, empty slots will be filled with filler items.
 * @constructor Create Chest GUI
 */
class ChestGUI private constructor(private var size: Int, private val holder: InventoryHolder?, private val isMenu: Boolean = false) {
    private var title: Component = Txt("Menu").c // Default title
    private val items: MutableMap<Int, ItemStack> = mutableMapOf()

    init { if (size !in 9..54 && size % 9 != 0) this.size = 27 }

    /**
     * Title
     *
     * @param title
     * @return
     */
    fun title(title: Component): ChestGUI {
        this.title = title
        return this
    }

    /**
     * Set item
     *
     * @param slot
     * @param item
     * @return
     */
    fun setItem(slot: Int, item: ItemStack): ChestGUI {
        require(!(slot < 0 || slot >= size)) { "Slot $slot is out of bounds" }
        items[slot] = item
        return this
    }

    /**
     * Set items
     *
     * @param range
     * @param item
     * @return
     */

    fun setItems(range: IntRange, item: ItemStack): ChestGUI {
        for (slot in range) {
            if (items[slot] == null) {
                setItem(slot, item)
            }
        }
        return this
    }

    /**
     * Build
     *
     * @return
     */
    fun build(): Inventory {
        val inventory = Bukkit.createInventory(holder, size, title)

        if (isMenu) {
            setItems(0..<size, ItemBuilder.builder(ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE))
                .filler()
                .build()
            )
        }

        items.forEach { (i: Int, item: ItemStack?) ->
            inventory.setItem(
                i, item
            )
        }

        return inventory
    }

    companion object {
        fun builder(size: Int, holder: InventoryHolder? = null, isMenu: Boolean): ChestGUI {
            return ChestGUI(size, holder, isMenu)
        }
    }
}