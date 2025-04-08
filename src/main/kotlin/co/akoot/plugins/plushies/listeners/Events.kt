package co.akoot.plugins.plushies.listeners

import co.akoot.plugins.plushies.listeners.handlers.stopMusic
import co.akoot.plugins.plushies.listeners.tasks.Golf.Companion.spawnGolfBall
import co.akoot.plugins.plushies.util.Util.getBlockPDC
import com.destroystokyo.paper.event.block.BlockDestroyEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockBurnEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.inventory.InventoryMoveItemEvent

class Events : Listener {

    @EventHandler
    fun blockPLace(event: BlockPlaceEvent) {
        event.isCancelled = spawnGolfBall(event.player, event.block.location.add(0.5, 0.0, 0.5))
    }

    @EventHandler
    fun BlockDestroyEvent.onBreak() {
        if (block.chunk.persistentDataContainer.has(getBlockPDC(block.location))) {
            stopMusic(block)
        }
    }

    @EventHandler
    fun BlockBreakEvent.onBreak() {
        if (block.chunk.persistentDataContainer.has(getBlockPDC(block.location))) {
            stopMusic(block)
        }
    }

    @EventHandler
    fun BlockBurnEvent.onBurn() {
        if (block.chunk.persistentDataContainer.has(getBlockPDC(block.location))) {
            stopMusic(block)
        }
    }

    @EventHandler
    fun onMove(event: InventoryMoveItemEvent) {
        // TODO: add disc swapping
        // cancel hopper move event for music disc
        val loc = event.destination.location ?: return
        val source = event.source.location ?: return
        if (loc.chunk.persistentDataContainer.has(getBlockPDC(loc)) ||
            source.chunk.persistentDataContainer.has(getBlockPDC(source))
        ) {
            event.isCancelled = true
        }
    }
}