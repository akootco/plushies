package co.akoot.plugins.plushies.listeners

import co.akoot.plugins.bluefox.extensions.getPDC
import co.akoot.plugins.bluefox.extensions.removePDC
import co.akoot.plugins.bluefox.extensions.setPDC
import co.akoot.plugins.bluefox.util.runLater
import co.akoot.plugins.plushies.util.*
import co.akoot.plugins.plushies.util.Util.getBlockPDC
import com.destroystokyo.paper.event.block.BlockDestroyEvent
import io.papermc.paper.event.block.BlockBreakBlockEvent
import io.papermc.paper.event.entity.EntityInsideBlockEvent
import org.bukkit.Effect
import org.bukkit.ExplosionResult
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.Tag
import org.bukkit.block.data.Levelled
import org.bukkit.entity.Display.Brightness
import org.bukkit.entity.Item
import org.bukkit.entity.ItemDisplay
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.*
import org.bukkit.event.entity.EntityExplodeEvent
import org.bukkit.util.Transformation
import org.joml.AxisAngle4f
import org.joml.Vector3f

class BlockEvents : Listener {

    @EventHandler
    fun BlockPlaceEvent.onPlace() {
        if (isCancelled) return // this needs to be checked so core protect doesn't break
        val hand = itemInHand.itemMeta ?: return
        val pdc = hand.getPDC<String>(blockKey) ?: hand.getPDC<String>(texturedkKey) ?: return
        val id = pdc.split("|").getOrNull(1)

        if (id != null) spawnItemDisplay(block.location.toCenterLocation(), itemInHand) {
            itemDisplayTransform = ItemDisplay.ItemDisplayTransform.FIXED
            brightness = Brightness(5, 15)
            transformation = Transformation(
                Vector3f(),
                AxisAngle4f(),
                Vector3f(2.001f),
                AxisAngle4f()
            )
        }
        block.chunk.setPDC(getBlockPDC(block.location), pdc)
        runLater(1) { block.chunk.removePDC(getBlockPDC(block.location, "alces")) }
    }

    @EventHandler
    fun BlockBreakEvent.onDestroy() {
        if (isCancelled) return // this needs to be checked so core protect doesn't break
        if (block.isCustomBlock) {
            val drops = block.state.drops
            if (drops.isNotEmpty()) {
                dropItems(block, drops.count())
                isDropItems = false
            }
            removeCustomBlock(block.location)
        }
    }

    @EventHandler
    fun BlockDestroyEvent.onDestroy() {
        if (isCancelled || !block.isCustomBlock) return
        val drops = block.state.drops
        if (drops.isNotEmpty()) {
            setWillDrop(false)
            dropItems(block, drops.count())
        }
        removeCustomBlock(block.location)
    }

    @EventHandler
    fun BlockBreakBlockEvent.onDestroy() {
        if (!block.isCustomBlock) return
        val drops = block.state.drops
        if (drops.isNotEmpty()) {
            dropItems(block, drops.count())
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
                    dropItems(it, drops.count())
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
                    dropItems(it, drops.count())
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

    @EventHandler
    fun EntityInsideBlockEvent.cauldronConcrete() {
        if (block.type != Material.WATER_CAULDRON) return

        val item = entity as? Item ?: return
        val type = item.itemStack.type
        val cauldron = block.blockData as? Levelled ?: return

        val result = when {
            Tag.ITEMS_CONCRETE_POWDERS.isTagged(type) ->
                Material.matchMaterial(type.name.substringBeforeLast("_")) ?: return

            Tag.ITEMS_DIRT.isTagged(type) -> Material.MUD

            else -> return
        }

        if (cauldron.level > 1) {
            cauldron.level -= 1
            block.blockData = cauldron
        } else block.type = Material.CAULDRON

        item.itemStack = item.itemStack.withType(result)

        item.world.apply {
            playEffect(block.location, Effect.EXTINGUISH, 0 )
            spawnParticle(Particle.POOF, block.location.toCenterLocation(),
                5, 0.2, 0.2, 0.2, 0.02) // ong?
        }
    }
}