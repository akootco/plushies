package co.akoot.plugins.plushies.gui

import co.akoot.plugins.bluefox.api.Kolor
import co.akoot.plugins.bluefox.extensions.invoke
import co.akoot.plugins.bluefox.util.ColorUtil.randomColor
import co.akoot.plugins.bluefox.util.Text
import co.akoot.plugins.plushies.api.ChestMenu
import co.akoot.plugins.plushies.util.builders.ItemBuilder
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class BookMenu(page: Int = 1) : ChestMenu(page, 45) {

    override val title =
        Text("Books")
            .color(randomColor(brightness = 0.6f))
            .component

    override val items: List<ItemStack>
        get() = (0 until 72).map { book ->
            ItemBuilder.builder(ItemStack(Material.WRITTEN_BOOK))
                .customModelData(book)
                .build()
        }

    override fun nextPage(): BookMenu = BookMenu(page + 1)

    override fun prevPage(): BookMenu = BookMenu(page - 1)

    override fun clickItem(player: Player, item: ItemStack) {
        if (item.type != Material.WRITTEN_BOOK) return

        val held = player.inventory.itemInMainHand

        if (held.type != Material.WRITTEN_BOOK) {
            Text(player) {
                Kolor.ERROR.accent("You must be holding a written book!")
            }
            return
        }

        player.inventory.setItemInMainHand(
            ItemBuilder.builder(held)
                .customModelData(item.itemMeta.customModelData)
                .build()
        )
    }
}