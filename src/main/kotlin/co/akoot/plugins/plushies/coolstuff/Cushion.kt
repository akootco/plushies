package co.akoot.plugins.plushies.coolstuff

import co.akoot.plugins.bluefox.extensions.isSurventure
import co.akoot.plugins.bluefox.util.text
import co.akoot.plugins.plushies.Plushies.Companion.key
import co.akoot.plugins.plushies.api.Interactable
import co.akoot.plugins.plushies.util.Items.applyDye
import co.akoot.plugins.plushies.util.builders.ItemBuilder
import org.bukkit.DyeColor
import org.bukkit.Material
import org.bukkit.Tag
import org.bukkit.block.BlockFace
import org.bukkit.entity.Interaction
import org.bukkit.entity.ItemDisplay
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerInteractEvent
import org.joml.Vector3f

object Cushion : Interactable {
    override val key = key("cushion")
    override val height = 0.25f
    override val translation = Vector3f(0f, 0.5f, 0f)
    override val cancelPlacement = true
    override val removable = true
    override val pushable = true
    override val useInteractionPoint = true

    override val item = ItemBuilder.builder(Material.WHITE_CARPET)
        .itemName("Cushion".text)
        .stackSize(16)
        .customModelData("cushion")
        .pdc(key)
        .build()
        .clone()

    override val breakSound = "block.wool.break"

    override fun interact(entity: Interaction, player: Player) {
        val item = player.inventory.itemInMainHand

        if (Tag.ITEMS_DYES.isTagged(item.type)) {
            val display = entity.vehicle as? ItemDisplay ?: return
            val itemStack = display.itemStack

            val color = DyeColor
                .valueOf(item.type.name.removeSuffix("_DYE"))
                .color

            if (!itemStack.applyDye(color)) return

            display.setItemStack(itemStack)

            if (player.isSurventure) item.amount--
            return
        }

        if (entity.passengers.isEmpty() && !player.isSneaking) {
            entity.addPassenger(player)
        }
    }

    override fun place(event: PlayerInteractEvent): Boolean {
        if (event.blockFace != BlockFace.UP) return false
        return super.place(event)
    }
}