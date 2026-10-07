package co.akoot.plugins.plushies.util

import co.akoot.plugins.bluefox.extensions.getPDC
import co.akoot.plugins.bluefox.extensions.hasPDC
import co.akoot.plugins.bluefox.extensions.removePDC
import co.akoot.plugins.bluefox.extensions.setPDC
import co.akoot.plugins.bluefox.util.runLater
import co.akoot.plugins.plushies.Plushies.Companion.key
import co.akoot.plugins.plushies.events.RemoveCustomBlockEvent
import co.akoot.plugins.plushies.util.Items.getItem
import co.akoot.plugins.plushies.util.Util.getBlockPDC
import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.block.Container
import org.bukkit.entity.ItemDisplay
import org.bukkit.util.BoundingBox

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

fun removeCustomBlock(location: Location) {
    RemoveCustomBlockEvent(location.block).call()
    // good trick!
    plugins.forEach {
        location.chunk.removePDC(getBlockPDC(location, it))
    }

    for (entity in location.world.getNearbyEntities(BoundingBox.of(location.block))) {
        if (entity is ItemDisplay) entity.remove()
    }
}

fun dropItems(block: Block, amount: Int) {
    val loc = block.location
    val key = loc.id?.split("|")?.get(0) ?: return
    repeat(amount) {
        loc.world.dropItemNaturally(block.location.toCenterLocation(), getItem(key) ?: return)
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


    for (entity in location.world.getNearbyEntities(BoundingBox.of(location.block))) {
        if (entity is ItemDisplay) entity.teleport(newLocation.toCenterLocation())
    }

    runLater(1) { newLocation.chunk.setPDC(getBlockPDC(newLocation), value) }
}