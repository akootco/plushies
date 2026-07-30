package co.akoot.plugins.plushies.coolstuff

import co.akoot.plugins.bluefox.extensions.hasPDC
import co.akoot.plugins.bluefox.extensions.setPDC
import co.akoot.plugins.plushies.Plushies.Companion.key
import co.akoot.plugins.plushies.util.spawnItemDisplay
import org.bukkit.Sound
import org.bukkit.block.BlockFace
import org.bukkit.entity.Entity
import org.bukkit.entity.Interaction
import org.bukkit.entity.ItemDisplay
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityMountEvent
import org.bukkit.event.player.PlayerInteractAtEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Transformation
import org.joml.AxisAngle4f
import org.joml.Vector3f

val cushionKey = key("cushion")

val Entity.isCushion: Boolean
    get() = hasPDC(cushionKey)

val ItemStack.isCushionItem: Boolean
    get() = hasPDC(cushionKey)

class Cushion() : Listener {

    @EventHandler
    fun PlayerInteractEvent.placeFurniture() {
        val support = clickedBlock ?: return
        val item = item ?: return

        if (action != Action.RIGHT_CLICK_BLOCK ||
            blockFace != BlockFace.UP ||
            !item.isCushionItem) return


        val yOffset = interactionPoint?.y?.minus(support.y)?.toFloat() ?: 1f

        isCancelled = true

        val display = spawnItemDisplay(
            support.location.add(0.0, yOffset.toDouble(), 0.0),
            item.asOne(),
            Transformation(
                Vector3f(0f, 0.5f, 0f),
                AxisAngle4f(),
                Vector3f(1f, 1f, 1f),
                AxisAngle4f()
            ),
            ItemDisplay.ItemDisplayTransform.HEAD
        )

        val interaction = display.world.spawn(
            display.location,
            Interaction::class.java
        ) {
            it.interactionWidth = 1f
            it.interactionHeight = .25f
            it.setPDC(key("cushion"), true)
        }

        display.addPassenger(interaction)

        item.amount -= 1
    }

    @EventHandler
    fun PlayerInteractAtEntityEvent.sit() {
        if (rightClicked.isCushion &&
            rightClicked.passengers.isEmpty() &&
            !player.isSneaking) {
            rightClicked.addPassenger(player)
        }
    }

    @EventHandler
    fun EntityDamageByEntityEvent.sit() {
        if (entity.isCushion && entity.passengers.isEmpty()) {
            removeCushion(entity)
        }
    }

    @EventHandler
    fun EntityMountEvent.denyKidnap() {
        isCancelled = entity.isCushion && mount is Player
    }

    fun removeCushion(cushion: Entity) {
        val display = cushion.vehicle as? ItemDisplay

        cushion.world.playSound(cushion.location, Sound.BLOCK_WOOL_BREAK, 1f, 1f)
        display?.itemStack?.clone()?.let {
            cushion.world.dropItemNaturally(cushion.location.add(0.0,0.5,0.0), it)
        }

        display?.remove()
        cushion.remove()
    }
}