package co.akoot.plugins.plushies.api

import co.akoot.plugins.bluefox.extensions.hasPDC
import co.akoot.plugins.plushies.util.createHitbox
import co.akoot.plugins.plushies.util.spawnItemDisplay
import org.bukkit.NamespacedKey
import org.bukkit.entity.Entity
import org.bukkit.entity.Interaction
import org.bukkit.entity.ItemDisplay
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Transformation
import org.joml.AxisAngle4f
import org.joml.Vector3f

object Interactables {
    private val values = mutableListOf<Interactable>()

    fun register(interactable: Interactable) {
        values += interactable
    }

    fun find(item: ItemStack): Interactable? =
        values.firstOrNull { item.hasPDC(it.key) }

    fun find(entity: Interaction): Interactable? =
        values.firstOrNull { entity.hasPDC(it.key) }
}

interface Interactable {

    val key: NamespacedKey

    val cancelPlacement: Boolean
        get() = false

    val placeSound: String?
        get() = null

    val breakSound: String?
        get() = null

    val scale: Float
        get() = 1f

    val width: Float
        get() = 1f

    val height: Float
        get() = 1f

    val translation: Vector3f
        get() = Vector3f(0f)

    val useInteractionPoint: Boolean
        get() = false

    val removable: Boolean
        get() = false

    val pushable: Boolean
        get() = false

    fun place(event: PlayerInteractEvent) {
        val support = event.clickedBlock ?: return
        val item = event.item ?: return

        val y = event.interactionPoint?.y?.minus(support.y) ?: 1.0

        val display = spawnItemDisplay(
            support.location.add(0.5, y, 0.5),
            item
        ) {
            transformation = Transformation(
                translation,
                AxisAngle4f(),
                Vector3f(scale),
                AxisAngle4f()
            )
        }.createHitbox(this)

        placeSound?.let {
            display.world.playSound(
                display.location,
                it,
                1f,
                1f
            )
        }

        item.amount--
    }

    fun remove(entity: Entity) {
        val display = entity.vehicle
        val location = entity.location
        val world = entity.world

        breakSound?.let {
            world.playSound(
                location,
                it,
                1f,
                1f
            )
        }

        if (display is ItemDisplay) {
            display.itemStack.clone().let {
                world.dropItemNaturally(
                    location.add(0.0, 0.8, 0.0),
                    it
                )
            }
        }

        display?.remove()
        entity.remove()
    }

    fun interact(
        entity: Interaction,
        player: Player
    )
}