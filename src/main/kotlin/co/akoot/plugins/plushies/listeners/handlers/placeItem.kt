package co.akoot.plugins.plushies.listeners.handlers

import co.akoot.plugins.bluefox.extensions.getPDC
import co.akoot.plugins.bluefox.extensions.removeMeta
import co.akoot.plugins.bluefox.extensions.setMeta
import co.akoot.plugins.bluefox.extensions.setPDC
import co.akoot.plugins.bluefox.util.runLater
import co.akoot.plugins.plushies.Plushies.Companion.key
import org.bukkit.block.BlockFace
import org.bukkit.entity.ItemFrame
import org.bukkit.entity.Player

val plushieFrame = key("plushie_iframe")

fun placeItem(face: BlockFace, player: Player) {
    val target = player.getTargetBlockExact(5) ?: return
    val location = target.location.add(0.5, 0.5, 0.5).add(face.direction)
    val item = player.inventory.itemInMainHand

    player.apply {
        swingMainHand() // fake the placement animation
        // set meta to cancel the upcoming block place event
        // if player is holding block in offhand
        setMeta("placedPlushie", true)
        runLater(2) {removeMeta("placedPlushie")}
        // item in offhand is still removed, fake news. update inventory for proof
        updateInventory()
    }

    target.world.spawn(location, ItemFrame::class.java) { frame ->
        frame.apply {
            setFacingDirection(face)
            isVisible = false
            setItem(item)
            setPDC(plushieFrame, true)
        }
    }

    item.amount -= 1 // remove item after setting frame item so it isn't set to air
}

fun dropItem(itemFrame: ItemFrame) {
    val loc = itemFrame.location
    if (itemFrame.getPDC<Boolean>(plushieFrame) != null) {
        loc.world.dropItemNaturally(loc.add(0.0,0.5,0.0), itemFrame.item)
        itemFrame.remove()
    }
}