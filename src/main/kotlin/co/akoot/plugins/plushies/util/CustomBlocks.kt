package co.akoot.plugins.plushies.util

import co.akoot.plugins.bluefox.extensions.getPDC
import co.akoot.plugins.bluefox.extensions.hasPDC
import co.akoot.plugins.bluefox.extensions.removePDC
import co.akoot.plugins.bluefox.extensions.setPDC
import co.akoot.plugins.bluefox.util.Text
import co.akoot.plugins.bluefox.util.runLater
import co.akoot.plugins.plushies.FurnitureUtil.isFurniture
import co.akoot.plugins.plushies.Plushies.Companion.key
import co.akoot.plugins.plushies.util.Items.customItems
import co.akoot.plugins.plushies.util.Util.getBlockPDC
import co.akoot.plugins.plushies.util.builders.ItemBuilder
import me.arcaniax.hdb.api.HeadDatabaseAPI
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.block.Container
import org.bukkit.block.data.Directional
import org.bukkit.block.data.Rotatable
import org.bukkit.entity.Display.Brightness
import org.bukkit.entity.EntityType
import org.bukkit.entity.ItemDisplay
import org.bukkit.inventory.ItemStack
import org.bukkit.util.BoundingBox
import org.bukkit.util.Transformation
import org.joml.AxisAngle4f
import org.joml.Vector3f

val blockKey = key("block_data")
val texturedkKey = key("block_textured")

val plugins = listOf("plushies", "edulis")

val Block.isCustomBlock: Boolean
    get() = plugins.any { pl ->
        location.chunk.hasPDC(getBlockPDC(location, pl))
    }

val Location.id: String?
    get() = plugins.firstNotNullOfOrNull { ns ->
        chunk.getPDC<String>(getBlockPDC(this, ns))
    }

fun spawnItemDisplay(
    location: Location,
    item: ItemStack,
    display: Transformation? = null
): ItemDisplay {
    val rotationMap = mapOf(
        BlockFace.SOUTH to 180f,
        BlockFace.SOUTH_EAST to 135f,
        BlockFace.EAST to 90f,
        BlockFace.NORTH_EAST to 45f,
        BlockFace.NORTH to 0f,
        BlockFace.NORTH_WEST to 315f,
        BlockFace.WEST to 270f,
        BlockFace.SOUTH_WEST to 225f
    )

    val fixedYaw = when (val blockData = location.block.blockData) {
        is Directional -> rotationMap[blockData.facing] ?: 0f
        is Rotatable -> rotationMap[blockData.rotation] ?: 0f
        else -> 180f
    }

    val itemDisplay = location.world.spawnEntity(
        location.toCenterLocation().apply { this.yaw = fixedYaw },
        EntityType.ITEM_DISPLAY
    ) as ItemDisplay

    itemDisplay.itemDisplayTransform = ItemDisplay.ItemDisplayTransform.FIXED
    itemDisplay.apply {
        setItemStack(item.clone().asOne())
        shadowRadius = 0f
        shadowStrength = 0f
        brightness = Brightness(5, 15)
        transformation =  display ?: Transformation(
            Vector3f(),
            AxisAngle4f(),
            Vector3f(2.001f, 2.001f, 2.001f),
            AxisAngle4f()
        )
    }

    return itemDisplay
}

fun createDisplay(location: Location, id: String, textured: Boolean = false) {
    val item = ItemBuilder.builder(
        if (textured) Material.OAK_PRESSURE_PLATE else Material.PLAYER_HEAD
    ).apply {
        if (textured) {
            val name = id.split("_").joinToString(" ") { it.replaceFirstChar { c -> c.titlecase() } }
            customModelData(id)
            itemModel("air")
            itemName(Text(name).component)
        } else {
            val headItem = HeadDatabaseAPI().getItemHead(id)
            if (headItem != null) copyOf(headItem)
            else headTexture(id)
        }
    }.build()

    spawnItemDisplay(location, item)
}

fun removeCustomBlock(location: Location) {
    // good trick!
    plugins.forEach {
        location.chunk.removePDC(getBlockPDC(location, it))
    }

    location.chunk.removePDC(getBlockPDC(location, "furniture.seat"))

    for (entity in location.world.getNearbyEntities(BoundingBox.of(location.block))) {
        if (entity is ItemDisplay) entity.remove()
    }
}

fun dropItems(block: Block, amount: Int) {
    val loc = block.location

    if (block.isFurniture) {
        val itemDisplay = loc.world.getNearbyEntities(
            BoundingBox.of(block)).filterIsInstance<ItemDisplay>().firstOrNull()
        val item = itemDisplay?.itemStack ?: return

        loc.world.dropItemNaturally(loc.toCenterLocation(), item)
    } else {
        val key = loc.id?.split("|")?.get(0) ?: return
        repeat(amount) {
            loc.world.dropItemNaturally(block.location.toCenterLocation(), customItems[key] ?: return)
        }
    }

    (block.state as? Container)?.inventory?.forEach { item ->
        if (item != null) {
            loc.world.dropItemNaturally(loc.toCenterLocation(), item)
        }
    }
}

fun handlePiston(location: Location, direction: BlockFace) {
    val value = location.chunk.getPDC<String>(getBlockPDC(location)) ?: return
    val newLocation =
        location.clone()
            .add(direction.modX.toDouble(), direction.modY.toDouble(), direction.modZ.toDouble())

    removeCustomBlock(location)
    createDisplay(newLocation, value.split("|").getOrNull(1) ?: "")

    runLater(1) { newLocation.chunk.setPDC(getBlockPDC(newLocation), value) }
}