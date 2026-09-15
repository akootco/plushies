package co.akoot.plugins.plushies.api

import co.akoot.plugins.bluefox.extensions.getPDC
import co.akoot.plugins.bluefox.extensions.hasPDC
import co.akoot.plugins.bluefox.extensions.isSurventure
import co.akoot.plugins.bluefox.extensions.setPDC
import co.akoot.plugins.plushies.Plushies.Companion.key
import co.akoot.plugins.plushies.util.createHitbox
import co.akoot.plugins.plushies.util.spawnItemDisplay
import net.kyori.adventure.text.Component
import org.bukkit.NamespacedKey
import org.bukkit.entity.Entity
import org.bukkit.entity.ItemDisplay
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Transformation
import org.joml.AxisAngle4f
import org.joml.Vector3f
import java.util.*
import kotlin.math.roundToInt

fun Player.owns(entity: Entity): Boolean =
    entity.getPDC<UUID>(key("owner")) == uniqueId

class InteractableItemMenu() : ChestMenu() {
    override val title = Component.text("Interactables")
    override val items = Interactables.items().toList()
}

object Interactables {
    private val values = mutableMapOf<NamespacedKey, Interactable>()
    private val items = mutableMapOf<NamespacedKey, ItemStack>()

    fun register(interactable: Interactable) {
        values[interactable.key] = interactable
        interactable.item?.let { items[interactable.key] = it }
    }

    fun find(item: ItemStack): Interactable? =
        values.values.firstOrNull { item.hasPDC(it.key) }

    fun find(entity: Entity): Interactable? =
        values.values.firstOrNull { entity.hasPDC(it.key) }

    fun items(): Collection<ItemStack> = items.values
}

interface Interactable {

    val key: NamespacedKey

    val item: ItemStack?
        get() = null

    val setOwner: Boolean
        get() = false

    val placeable: Boolean
        get() = true

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

    val rotatable: Boolean
        get() = false

    fun place(event: PlayerInteractEvent): Boolean {
        val support = event.clickedBlock ?: return false
        val item = event.item ?: return false

        val y = if (useInteractionPoint) {
            event.interactionPoint?.y?.minus(support.y) ?: 1.0
        } else {
            1.0
        }

        val rotation = if (rotatable)
            Math.toRadians(
                -((event.player.yaw / 45f).roundToInt() * 45f + 180f).toDouble()
            ).toFloat()
        else 0f

        val display = spawnItemDisplay(
            support.location.add(0.5, y, 0.5),
            item
        ) {
            transformation = Transformation(
                translation,
                AxisAngle4f(rotation, 0f, 1f, 0f),
                Vector3f(scale),
                AxisAngle4f()
            )
        }

        display.createHitbox(this).apply {
            if (setOwner) setPDC(key("owner"), event.player.uniqueId)
        }

        placeSound?.let {
            display.world.playSound(
                display.location,
                it,
                1f,
                1f
            )
        }

        if (event.player.isSurventure) item.amount--
        return true
    }

    fun remove(entity: Entity, damager: Entity? = null): Boolean {
        if (setOwner && (damager !is Player || !damager.owns(entity))) return false

        val display = entity.vehicle
        val location = entity.location
        val world = entity.world

        breakSound?.let {
            world.playSound(location, it, 1f, 1f)
        }

        if (display is ItemDisplay) {
            world.dropItemNaturally(
                location.add(0.0, 0.8, 0.0),
                display.itemStack.clone()
            )
        }

        display?.remove()
        entity.remove()
        return true
    }

    fun interact(player: Player) {}

    fun interact(
        entity: Entity,
        player: Player
    ) {}
}