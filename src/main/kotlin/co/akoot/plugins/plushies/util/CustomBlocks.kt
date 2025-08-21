package co.akoot.plugins.plushies.util

import co.akoot.plugins.bluefox.extensions.getPDC
import co.akoot.plugins.bluefox.extensions.hasPDC
import co.akoot.plugins.bluefox.extensions.removePDC
import co.akoot.plugins.bluefox.extensions.setPDC
import co.akoot.plugins.bluefox.util.runLater
import co.akoot.plugins.plushies.Plushies.Companion.key
import co.akoot.plugins.plushies.util.Items.customItems
import co.akoot.plugins.plushies.util.Util.getBlockPDC
import co.akoot.plugins.plushies.util.builders.ItemBuilder
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.entity.EntityType
import org.bukkit.entity.ItemDisplay
import org.bukkit.inventory.ItemStack
import org.bukkit.util.BoundingBox
import org.bukkit.util.Transformation
import org.joml.AxisAngle4f
import org.joml.Vector3f

val blockKey = key("block_data")
val plugins = listOf("plushies", "edulis")

val Block.isCustomBlock: Boolean
    get() = plugins.any { pl ->
        location.chunk.hasPDC(getBlockPDC(location, pl))
    }

val Location.id: String?
    get() = plugins.firstNotNullOfOrNull { ns ->
        chunk.getPDC<String>(getBlockPDC(this, ns))
    }


fun createDisplay(location: Location, id: String) {
    val overlay = ItemBuilder.builder(ItemStack(Material.PLAYER_HEAD))
        .headTexture(id)
        .build()

    val itemDisplay =
        location.world.spawnEntity(location.toCenterLocation(), EntityType.ITEM_DISPLAY) as ItemDisplay
    itemDisplay.apply {
        setItemStack(overlay)
        transformation = Transformation(
            Vector3f(0f, .501f, 0f),
            AxisAngle4f(0f, 1f, 0f, 0f),
            Vector3f(2.001f, 2.004f, 2.001f),
            AxisAngle4f(0f, 1f, 0f, 0f)
        )
    }
}

fun removeCustomBlock(location: Location) {
    // good trick!
    plugins.forEach {
        location.chunk.removePDC(getBlockPDC(location, it))
    }

    for (entity in location.world.getNearbyEntities(BoundingBox.of(location.block))) {
        if (entity is ItemDisplay) entity.remove()
    }
}

fun dropItems(location: Location, amount: Int) {
    val key = location.id?.split("|")?.get(0) ?: return
    repeat(amount) {
        location.world.dropItemNaturally(location.toCenterLocation(), customItems[key] ?: return)
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