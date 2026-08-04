package co.akoot.plugins.plushies.coolstuff

import co.akoot.plugins.bluefox.extensions.hasPDC
import co.akoot.plugins.bluefox.extensions.setPDC
import co.akoot.plugins.plushies.Plushies.Companion.key
import co.akoot.plugins.plushies.util.Items.applyDye
import co.akoot.plugins.plushies.util.spawnItemDisplay
import org.bukkit.DyeColor
import org.bukkit.Sound
import org.bukkit.Tag
import org.bukkit.block.BlockFace
import org.bukkit.entity.Entity
import org.bukkit.entity.Interaction
import org.bukkit.entity.ItemDisplay
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockPistonExtendEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityMountEvent
import org.bukkit.event.player.PlayerInteractAtEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.util.BoundingBox

val cushionKey = key("cushion")

val Entity.isCushion: Boolean
    get() = hasPDC(cushionKey)

val ItemStack.isCushionItem: Boolean
    get() = hasPDC(cushionKey)

class Cushion() : Listener {

    @EventHandler
    fun PlayerInteractEvent.place() {
        if (isCancelled) return
        val support = clickedBlock ?: return
        val item = item ?: return

        if (action != Action.RIGHT_CLICK_BLOCK) return
        if (blockFace != BlockFace.UP) return
        if (!item.isCushionItem) return

        isCancelled = true

        val display = spawnItemDisplay(
            support.location.add(
                0.5,
                interactionPoint?.y?.minus(support.y) ?: 1.0,
                0.5
            ),
            item
        )

        display.addPassenger(
            display.world.spawn(display.location, Interaction::class.java) { seat ->
                seat.interactionWidth = 1f
                seat.interactionHeight = 0.25f
                seat.setPDC(cushionKey, true)
            }
        )

        item.amount--
    }

    @EventHandler
    fun PlayerInteractAtEntityEvent.sit() {
        if (!rightClicked.isCushion) return

        val item = player.inventory.itemInMainHand

        when {
            Tag.ITEMS_DYES.isTagged(item.type) -> {
                val display = rightClicked.vehicle as? ItemDisplay ?: return
                val cushion = display.itemStack

                val color = DyeColor.valueOf(item.type.name.removeSuffix("_DYE")).color
                if (!cushion.applyDye(color)) return

                display.setItemStack(cushion)
                item.amount--
            }

            rightClicked.passengers.isEmpty() && !player.isSneaking -> {
                rightClicked.addPassenger(player)
            }
        }
    }

    @EventHandler
    fun EntityDamageByEntityEvent.remove() {
        if (entity.isCushion && entity.passengers.isEmpty()) {
            removeCushion(entity)
        }
    }

    @EventHandler
    fun BlockPistonExtendEvent.pistonBreak() {
        val pushedTo = block.getRelative(direction)
        pushedTo.world
            .getNearbyEntities(BoundingBox.of(pushedTo))
            .filter { it.isCushion }
            .forEach(::removeCushion)
    }

    @EventHandler
    fun EntityMountEvent.denyKidnap() {
        isCancelled = entity.isCushion && mount is Player
    }

    fun removeCushion(cushion: Entity) {
        val display = cushion.vehicle as? ItemDisplay
        val location = cushion.location
        val world = cushion.world

        world.playSound(location, Sound.BLOCK_WOOL_BREAK, 1f, 1f)

        display?.itemStack?.clone()?.let {
            world.dropItemNaturally(location.add(0.0, 0.5, 0.0), it)
            display.remove()
        }

        cushion.remove()
    }
}