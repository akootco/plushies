package co.akoot.plugins.plushies.api

import co.akoot.plugins.bluefox.extensions.getPDC
import co.akoot.plugins.bluefox.extensions.hasPDC
import co.akoot.plugins.bluefox.extensions.isSurventure
import co.akoot.plugins.bluefox.extensions.setPDC
import co.akoot.plugins.plushies.Plushies.Companion.key
import co.akoot.plugins.plushies.api.Interactables.hasInteractable
import co.akoot.plugins.plushies.util.createHitbox
import co.akoot.plugins.plushies.util.spawnItemDisplay
import io.papermc.paper.datacomponent.DataComponentTypes
import net.kyori.adventure.text.Component
import org.bukkit.NamespacedKey
import org.bukkit.block.Block
import org.bukkit.entity.Entity
import org.bukkit.entity.ItemDisplay
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.util.BoundingBox
import org.bukkit.util.Transformation
import org.joml.AxisAngle4f
import org.joml.Vector3f
import java.util.*
import kotlin.math.roundToInt

fun Player.owns(entity: Entity): Boolean =
    entity.getPDC<UUID>(key("owner")) == uniqueId

data class Sound(
    val sound: String,
    val pitch: Float = 1f,
    val volume: Float = 1f,
)

class InteractableItemMenu() : ChestMenu() {
    override val title = Component.text("Interactables")
    override val items = Interactables.items().toList()
}

object Interactables {
    private val values = mutableMapOf<NamespacedKey, Interactable>()
    val items = mutableMapOf<NamespacedKey, ItemStack>()

    fun register(interactable: Interactable) {
        values[interactable.key] = interactable
        interactable.item()?.let { items[interactable.key] = it }
    }

    fun find(item: ItemStack): Interactable? =
        values.values.firstOrNull { item.hasPDC(it.key) }

    fun find(entity: Entity): Interactable? =
        values.values.firstOrNull { entity.hasPDC(it.key) }

    fun Block.hasInteractable(): Boolean =
        world.getNearbyEntities(BoundingBox.of(this)) { find(it) != null }.isNotEmpty()

    fun items(): Collection<ItemStack> = items.values
}

interface Interactable {

    val key: NamespacedKey

    fun item(): ItemStack? = null

    val setOwner: Boolean
        get() = false

    val placeable: Boolean
        get() = true

    val cancelPlacement: Boolean
        get() = false

    val placeSound: Sound?
        get() = null

    val breakSound: Sound?
        get() = null

    fun scale(): Float = 1f

    fun width(): Float = 1f

    fun height(): Float = 1f

    fun translation(): Vector3f = Vector3f(0f)

    val useInteractionPoint: Boolean
        get() = false

    fun y(): Double = 1.0

    val removable: Boolean
        get() = false

    val pushable: Boolean
        get() = false

    fun rotation(): Float = 0f

    fun place(event: PlayerInteractEvent): Boolean = placeEntity(event) != null

    fun placeEntity(event: PlayerInteractEvent): Entity? {
        if (event.hand != EquipmentSlot.HAND) return null
        val support = event.clickedBlock ?: return null
        val item = event.item ?: return null

        if (support.getRelative(event.blockFace).hasInteractable()) return null

        event.setUseInteractedBlock(Event.Result.DENY)
        event.setUseItemInHand(Event.Result.DENY)

        val y = if (useInteractionPoint) { event.interactionPoint?.y?.minus(support.y) ?: y() } else { y() }
        val rotation = (event.player.yaw / rotation()).roundToInt() * rotation() + 180f

        val display = spawnItemDisplay(
            support.location.add(0.5, y, 0.5),
            item
        ) {
            setRotation(rotation, 0f)
            transformation = Transformation(
                translation(),
                AxisAngle4f(),
                Vector3f(scale()),
                AxisAngle4f()
            )
        }

        val hitbox = display.createHitbox(this).apply {
            setRotation(rotation, 0f)
            if (setOwner) setPDC(key("owner"), event.player.uniqueId)
        }

        placeSound?.let {
            display.world.playSound(
                display.location,
                it.sound,
                it.volume,
                it.pitch
            )
        }

        event.player.swingMainHand()
        if (event.player.isSurventure) item.amount--
        return hitbox
    }

    fun remove(entity: Entity, damager: Entity? = null): Boolean {
        if (setOwner && (damager !is Player || (!damager.isOp && !damager.owns(entity)))) return false

        val display = entity.vehicle
        val location = entity.location
        val world = entity.world

        breakSound?.let { world.playSound(location, it.sound, it.volume, it.pitch) }

        if (display is ItemDisplay) {
            world.dropItemNaturally(
                location.add(0.0, 0.8, 0.0),
                display.itemStack.clone().apply {
                    unsetData(DataComponentTypes.CUSTOM_MODEL_DATA)
                }
            )
        }

        display?.remove()
        entity.remove()
        return true
    }

    fun interact(player: Player) { player.swingMainHand() }

    fun interact(
        entity: Entity,
        player: Player
    ) {}
}