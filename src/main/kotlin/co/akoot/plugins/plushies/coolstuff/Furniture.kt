package co.akoot.plugins.plushies.coolstuff

import co.akoot.plugins.bluefox.api.FoxConfig
import co.akoot.plugins.bluefox.extensions.getMeta
import co.akoot.plugins.bluefox.extensions.getPDC
import co.akoot.plugins.bluefox.extensions.hasPDC
import co.akoot.plugins.bluefox.extensions.setMeta
import co.akoot.plugins.plushies.Plushies
import co.akoot.plugins.plushies.Plushies.Companion.key
import co.akoot.plugins.plushies.api.Interactable
import co.akoot.plugins.plushies.api.Interactables
import co.akoot.plugins.plushies.api.Storage
import co.akoot.plugins.plushies.api.openMenu
import co.akoot.plugins.plushies.util.ItemCreator.createItem
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.Sound
import org.bukkit.block.BlockFace
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.util.BoundingBox
import org.joml.Vector3f

class ConfigFurniture(val id: String) : Interactable {

    private val path = "$id.furniture"

    override val key: NamespacedKey = key(id)
    override val removable = true
    override val pushable = true

    override fun item(): ItemStack? =
        createItem(config, id, key(id)) { Material.POISONOUS_POTATO }?.clone()

    override val rotatable = config.getBoolean("$path.rotatable") ?: super.rotatable
    override val placeSound = config.getString("$path.sound.place") ?: super.placeSound
    override val breakSound = config.getString("$path.sound.break") ?: super.breakSound

    private val width = config.getDouble("$path.width")?.toFloat() ?: super.width()
    private val height = config.getDouble("$path.height")?.toFloat() ?: super.height()
    private val collision = config.getString("$path.collision")

    override fun translation() = Vector3f(0f,0.5f,0f)
    override fun width() = width
    override fun height() = height

    private val isContainer = config.getKeys(path).contains("container")
    private val rows = config.getInt("$path.container.rows") ?: 1

    private val inventoryType =
        config.getEnum<InventoryType>("$path.container.type")
            ?.takeIf { it.isCreatable }
            ?: InventoryType.CHEST

    private val title = Component.text(config.getString("$path.container.title") ?: config.getString("$id.itemName") ?: id)

    private val isSeat = config.getBoolean("$path.seat") ?: false

    private fun storageOf(entity: Entity): FurnitureStorage =
        entity.getMeta<FurnitureStorage>("furniture.storage")
            ?: FurnitureStorage(entity, path, title, rows, inventoryType).also {
                entity.setMeta("furniture.storage", it)
                it.loadContents()
            }


    override fun place(event: PlayerInteractEvent): Boolean {
        val clicked = event.clickedBlock ?: return false
        val above = clicked.getRelative(BlockFace.UP)

        if (event.blockFace != BlockFace.UP || above.type != Material.AIR) return false
        if (clicked.world.getNearbyEntities(BoundingBox.of(above)).any { it.hasPDC(key) }) return false

        when (collision) {
            "half" -> {}
            "full" -> {}
        }

        return super.place(event)
    }

    override fun interact(entity: Entity, player: Player) {
        when {
            isContainer && (!isSeat || player.isSneaking) ->
                player.openMenu(storageOf(entity))

            isSeat && entity.passengers.isEmpty() -> {
                entity.addPassenger(player)
                val rotation = entity.getPDC<Float>(key("rotation")) ?: 0f
                player.setRotation(Math.toDegrees(-rotation.toDouble()).toFloat(), 1f)
            }
        }
    }

    override fun remove(entity: Entity, damager: Entity?): Boolean {
        if (isContainer) storageOf(entity).remove()
        collision.let { entity.location.block.type = Material.AIR }
        return super.remove(entity, damager)
    }

    companion object {
        internal val config: FoxConfig =
            Plushies.instance.registerConfig("furniture")

        fun loadFurniture() {
            for (id in config.getKeys()) {
                val furniture = ConfigFurniture(id)
                if (furniture.item() == null) continue
                Interactables.register(furniture)
            }
        }
    }
}

private class FurnitureStorage(
    override val entity: Entity,
    private val path: String,
    title: Component,
    rows: Int,
    inventoryType: InventoryType
) : Storage(title, "storage", StorageConfig(inventoryType, rows)) {

    override val openSound: StorageSound?
        get() = ConfigFurniture.config
            .getString("$path.container.sound.open")
            ?.let { StorageSound(it) }

    override val closeSound: StorageSound?
        get() = ConfigFurniture.config
            .getString("$path.container.sound.close")
            ?.let { StorageSound(it) }
}