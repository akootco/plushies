package co.akoot.plugins.plushies.api

import co.akoot.plugins.bluefox.util.Text
import co.akoot.plugins.plushies.util.builders.ItemBuilder
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack
import kotlin.math.min

abstract class ChestMenu(
    protected val size: Int = 54
) : InventoryHolder {

    companion object {
        val nextPage = ItemBuilder.builder(ItemStack(Material.PAPER))
            .itemName(Text("→", NamedTextColor.GOLD).component).build()

        val prevPage = ItemBuilder.builder(ItemStack(Material.PAPER))
            .itemName(Text("←", NamedTextColor.GOLD).component).build()

        val filler = ItemBuilder.builder(ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE))
            .filler().build()
    }

    init {
        require(size in 9..54 && size % 9 == 0) {
            "Invalid chest size: $size"
        }
    }

    abstract val title: Component
    private var page = 1
    abstract val items: List<ItemStack>

    private val itemSlots = size - 9

    override fun getInventory(): Inventory {
        val inventory = Bukkit.createInventory(this, size, title)

        for (slot in 0 until size) {
            inventory.setItem(slot, filler)
        }

        if (page > 1) {
            inventory.setItem(size - 9, prevPage)
        }

        if (items.size > page * itemSlots) {
            inventory.setItem(size - 1, nextPage)
        }

        getPageItems().forEachIndexed { index, item ->
            inventory.setItem(index, item)
        }

        return inventory
    }

    private fun getPageItems(): List<ItemStack> {
        val start = (page - 1) * itemSlots
        val end = min(start + itemSlots, items.size)

        return items.subList(start, end)
    }

    open fun onClick(event: InventoryClickEvent) {
        val player = event.whoClicked as? Player ?: return
        val item = event.currentItem ?: return

        when (item) {
            filler -> return

            nextPage -> {
                page++
                player.openInventory(inventory)
                return
            }

            prevPage -> {
                if (page > 1) page--
                player.openInventory(inventory)
                return
            }
        }

        clickItem(player, item, event)
    }

    open fun clickItem(player: Player, item: ItemStack) {
        if (player.isOp || player.gameMode == GameMode.CREATIVE) {
            player.give(item)
        }
    }

    open fun clickItem(player: Player, item: ItemStack, event: InventoryClickEvent) {
        clickItem(player, item)
    }
}