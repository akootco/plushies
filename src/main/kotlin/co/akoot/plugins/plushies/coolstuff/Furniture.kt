package co.akoot.plugins.plushies.coolstuff

import co.akoot.plugins.bluefox.api.FoxConfig
import co.akoot.plugins.bluefox.extensions.getMeta
import co.akoot.plugins.bluefox.extensions.setMeta
import co.akoot.plugins.bluefox.util.parse
import co.akoot.plugins.plushies.Plushies
import co.akoot.plugins.plushies.Plushies.Companion.key
import co.akoot.plugins.plushies.api.*
import co.akoot.plugins.plushies.util.ItemCreator.createItem
import io.papermc.paper.datacomponent.DataComponentTypes
import io.papermc.paper.datacomponent.item.ResolvableProfile
import net.kyori.adventure.key.Key
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Tag
import org.bukkit.block.BlockFace
import org.bukkit.block.Skull
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

        collision?.let { above.location.addCollision(it) }

        val hitbox = super.placeEntity(event) ?: return null
        return hitbox
    }

    override fun interact(entity: Entity, player: Player) {
        when {
            isContainer && (!isSeat || player.isSneaking) ->
                player.openMenu(storageOf(entity))

            isSeat && entity.passengers.isEmpty() -> {
                entity.addPassenger(player)
                player.setRotation(entity.yaw, 1f)
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

                val types = mutableListOf<String>()
                if ("wood" in variants) types += Tag.PLANKS.values.map { it.name.removeSuffix("_PLANKS").lowercase() }
                if ("stone" in variants) types += Tag.BASE_STONE_OVERWORLD.values.map { it.name.lowercase() }

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

private fun Location.addCollision(type: String) {
    when (type) {
        "half" -> halfBlock()
        "full" -> {}
    }
}

private fun Location.halfBlock() {
    val state = Bukkit.createBlockData(Material.PLAYER_HEAD)
        .createBlockState()
        .copy(this) as Skull

    state.profile = ResolvableProfile.resolvableProfile()
        .skinPatch(
            ResolvableProfile.SkinPatch.skinPatch()
                .body(Key.key("block/beetroots_stage0"))
                .build()
        )
        .build()

    state.update(true, false)
}