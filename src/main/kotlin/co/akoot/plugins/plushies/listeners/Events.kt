package co.akoot.plugins.plushies.listeners

import co.akoot.plugins.bluefox.extensions.getMeta
import co.akoot.plugins.bluefox.extensions.getPDC
import co.akoot.plugins.plushies.listeners.handlers.dropItem
import co.akoot.plugins.plushies.listeners.handlers.plushieFrame
import co.akoot.plugins.plushies.listeners.tasks.Golf.Companion.golfKey
import co.akoot.plugins.plushies.listeners.tasks.Golf.Companion.spawnGolfBall
import io.papermc.paper.event.player.PlayerItemFrameChangeEvent
import io.papermc.paper.event.player.PlayerItemFrameChangeEvent.ItemFrameChangeAction
import org.bukkit.entity.ItemFrame
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.hanging.HangingBreakEvent
import org.bukkit.event.hanging.HangingPlaceEvent
import org.bukkit.event.player.PlayerArmorStandManipulateEvent

class Events : Listener {

    @EventHandler
    fun armorStandEdit(event: PlayerArmorStandManipulateEvent) {
        val clicked = event.rightClicked
        if (clicked.getPDC<String>(golfKey) != null) {
            clicked.remove()
        }
    }

    @EventHandler
    fun blockPlace(event: BlockPlaceEvent) {
        event.isCancelled = (event.player.getMeta<Boolean>("placedPlushie") == true) ||
                spawnGolfBall(event.player, event.block.location.add(0.5, 0.0, 0.5))
    }

    @EventHandler
    fun blockPlace(event: HangingPlaceEvent) {
        val player = event.player ?: return
        event.isCancelled = (player.getMeta<Boolean>("placedPlushie") == true)
    }

    @EventHandler
    fun PlayerItemFrameChangeEvent.frameChange() {
        if (itemFrame.getPDC<Boolean>(plushieFrame) != null &&
            action == ItemFrameChangeAction.REMOVE) { itemFrame.remove() }
    }

    @EventHandler
    fun itemFrameBreak(event: HangingBreakEvent) {
        dropItem(event.entity as? ItemFrame ?: return)
    }
}