package co.akoot.plugins.plushies.gui

import co.akoot.plugins.bluefox.api.Kolor
import co.akoot.plugins.bluefox.extensions.invoke
import co.akoot.plugins.bluefox.util.ColorUtil.randomColor
import co.akoot.plugins.bluefox.util.Text
import co.akoot.plugins.bluefox.util.Text.Companion.asString
import co.akoot.plugins.plushies.api.ChestMenu
import co.akoot.plugins.plushies.items.PlushieItems.plushies
import co.akoot.plugins.plushies.util.Util.plushMsg
import co.akoot.plugins.plushies.util.builders.ItemBuilder
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

class PlushieMenu(page: Int = 1) : ChestMenu(page) {

    override val title =
        Text("Plushies")
            .color(randomColor(brightness = 0.6f))
            .component

    override val items: List<ItemStack>
        get() = plushies
            .sortedBy { it.name.lowercase() }
            .map { it.item() }

    override fun nextPage() = PlushieMenu(page + 1)

    override fun prevPage() = PlushieMenu(page - 1)

    override fun clickItem(
        player: Player,
        item: ItemStack,
        event: InventoryClickEvent
    ) {
        if (item.type != Material.TOTEM_OF_UNDYING) return

        val held = player.inventory.itemInMainHand

        if (held.type != Material.TOTEM_OF_UNDYING) {
            Kolor.ERROR.accent("You must be holding a totem!").send(player)
            return
        }

        val builder = ItemBuilder.builder(held)
            .copyOf(item)

        if (event.click == ClickType.RIGHT) {
            val cmd = held.itemMeta.customModelDataComponent

            builder.customModelData(
                cmd.floats?.firstOrNull()?.plus(1)?.toInt()
                    ?: (cmd.strings?.firstOrNull() + ".st")
            )
        }

        builder.build()

        plushMsg(item.effectiveName().asString()).component
    }
}