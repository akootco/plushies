package co.akoot.plugins.plushies.listeners

import co.akoot.plugins.bluefox.extensions.getPDC
import co.akoot.plugins.bluefox.extensions.removePDC
import co.akoot.plugins.bluefox.extensions.setPDC
import co.akoot.plugins.bluefox.util.runLater
import co.akoot.plugins.plushies.util.*
import co.akoot.plugins.plushies.util.Util.getBlockPDC
import com.destroystokyo.paper.event.block.BlockDestroyEvent
import io.papermc.paper.event.block.BlockBreakBlockEvent
import org.bukkit.ExplosionResult
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.*
import org.bukkit.event.entity.EntityExplodeEvent

class BlockEvents : Listener {

    @EventHandler
    fun BlockPlaceEvent.onPlace() {
        if (isCancelled) return // this needs to be checked so core protect doesn't break
        val id = itemInHand.itemMeta.getPDC<String>(blockKey) ?: return

        createDisplay(block.location, id.split("|")[1])
        block.chunk.setPDC(getBlockPDC(block.location), id)
        runLater(1) { block.chunk.removePDC(getBlockPDC(block.location, "alces")) }
    }

    @EventHandler
    fun BlockBreakEvent.onDestroy() {
        if (block.isCustomBlock) {
            val drops = block.state.drops
            if (drops.isNotEmpty()) {
                dropItems(block.location, drops.count())
                isDropItems = false
            }
        }
        removeCustomBlock(block.location)
    }

    @EventHandler
    fun BlockDestroyEvent.onDestroy() {
        if (isCancelled || !block.isCustomBlock) return
        val drops = block.state.drops
        if (drops.isNotEmpty()) {
            setWillDrop(false)
            dropItems(block.location, drops.count())
        }
        removeCustomBlock(block.location)
    }

    @EventHandler
    fun BlockBreakBlockEvent.onDestroy() {
        if (!block.isCustomBlock) return
        val drops = block.state.drops
        if (drops.isNotEmpty()) {
            dropItems(block.location, drops.count())
            drops.clear()
        }
        removeCustomBlock(block.location)
    }

    @EventHandler
    fun BlockExplodeEvent.explode() {
        if (isCancelled || explosionResult != ExplosionResult.DESTROY) return
        blockList().filter { it.isCustomBlock }
            .forEach {
                val drops = it.state.drops
                if (drops.isNotEmpty()) {
                    dropItems(it.location, drops.count())
                    drops.clear()
                }
                removeCustomBlock(it.location)
            }
    }

    @EventHandler
    fun EntityExplodeEvent.explode() {
        if (isCancelled || explosionResult != ExplosionResult.DESTROY) return
        blockList().filter { it.isCustomBlock }
            .forEach {
                val drops = it.state.drops
                if (drops.isNotEmpty()) {
                    dropItems(it.location, drops.count())
                    drops.clear()
                }
                removeCustomBlock(it.location)
            }
    }

    // piston events need to be ran 1 tick later so the BlockBreakBlockEvent has a chance to do its job
    @EventHandler
    fun BlockPistonRetractEvent.pistonRetract() {
        if (isCancelled) return
        blocks.filter { it.isCustomBlock }
            .forEach { runLater(1) { handlePiston(it.location, direction) } }
    }

    @EventHandler
    fun BlockPistonExtendEvent.pistonExtend() {
        if (isCancelled) return
        blocks.filter { it.isCustomBlock }
            .forEach { runLater(1) { handlePiston(it.location, direction) } }
    }
}