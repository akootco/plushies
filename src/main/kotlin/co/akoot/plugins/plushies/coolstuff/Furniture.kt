package co.akoot.plugins.plushies.coolstuff

import co.akoot.plugins.bluefox.api.FoxConfig
import co.akoot.plugins.bluefox.extensions.getMeta
import co.akoot.plugins.bluefox.extensions.getPDC
import co.akoot.plugins.bluefox.extensions.setMeta
import co.akoot.plugins.bluefox.util.parse
import co.akoot.plugins.plushies.Plushies
import co.akoot.plugins.plushies.Plushies.Companion.key
import co.akoot.plugins.plushies.api.Interactable
import co.akoot.plugins.plushies.api.Interactables
import co.akoot.plugins.plushies.api.Sound
import co.akoot.plugins.plushies.api.Storage
import co.akoot.plugins.plushies.api.openMenu
import co.akoot.plugins.plushies.util.ItemCreator.createItem
import io.papermc.paper.datacomponent.DataComponentTypes
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.Tag
import org.bukkit.block.BlockFace
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.joml.Vector3f

class ConfigFurniture(val id: String) : Interactable {

    private val path = "$id.furniture"
    override val key = key(id)
    override val removable = true
    override val pushable = true

    override fun item(): ItemStack? = createItem(config, id, key) { Material.POISONOUS_POTATO }?.clone()

    override fun rotation() = config.getDouble("$path.rotation")?.toFloat() ?: super.rotation()

    override val placeSound = sound("$path.sound.place") ?: super.placeSound
    override val breakSound = sound("$path.sound.break") ?: super.breakSound

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

    private val title = (config.getString("$path.container.title") ?: config.getString("$id.itemName") ?: id).parse()

    private val isSeat = config.getBoolean("$path.seat") ?: false

    private fun storageOf(entity: Entity): FurnitureStorage =
        entity.getMeta<FurnitureStorage>("furniture.storage")
            ?: FurnitureStorage(entity, path, title, rows, inventoryType).also {
                entity.setMeta("furniture.storage", it)
                it.loadContents()
            }


    override fun placeEntity(event: PlayerInteractEvent): Entity? {
        val clicked = event.clickedBlock ?: return null
        val above = clicked.getRelative(BlockFace.UP)

        if (event.blockFace != BlockFace.UP || above.type != Material.AIR) return null

        when (collision) {
            "half" -> {}
            "full" -> {}
        }

        val hitbox = super.placeEntity(event) ?: return null
        return hitbox
    }

    override fun interact(entity: Entity, player: Player) {
        when {
            isContainer && (!isSeat || player.isSneaking) ->
                player.openMenu(storageOf(entity))

            isSeat && entity.passengers.isEmpty() -> {
                entity.addPassenger(player)
                val rotation = entity.getPDC<Float>(key("rotation")) ?: 0f
                player.setRotation(rotation, 1f)
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
                val variants = config.getStringList("$id.variants")

                val types = when {
                    variants.contains("wood") -> Tag.PLANKS.values.map { it.name.removeSuffix("_PLANKS").lowercase() }
                    variants.contains("stone") -> Tag.BASE_STONE_OVERWORLD.values.map { it.name.lowercase() }
                    else -> emptyList()
                }

                val furniture = ConfigFurniture(id)
                val item = furniture.item() ?: continue
                Interactables.register(furniture)

                for (variant in types) {
                    val name = key("${variant}_$id")
                    Interactables.items[name] = item.clone().apply {
                        setData(DataComponentTypes.ITEM_MODEL, name)
                    }
                }
                //delete base
                if (types.isNotEmpty()) Interactables.items.remove(furniture.key)
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

    override val openSound get() = sound("$path.container.sound.open")
    override val closeSound get() = sound("$path.container.sound.close")
}

private fun sound(path: String): Sound? = with(ConfigFurniture.config) {
    Sound(
        getString("$path.sound") ?: return null,
        getDouble("$path.pitch")?.toFloat() ?: 1f,
        getDouble("$path.volume")?.toFloat() ?: 1f
    )
}