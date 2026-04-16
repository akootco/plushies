package co.akoot.plugins.plushies

import co.akoot.plugins.bluefox.extensions.getPDC
import co.akoot.plugins.bluefox.extensions.hasPDC
import co.akoot.plugins.bluefox.extensions.setPDC
import co.akoot.plugins.bluefox.util.runLater
import co.akoot.plugins.plushies.FurnitureUtil.createHalfBlock
import co.akoot.plugins.plushies.FurnitureUtil.furnitureHitBox
import co.akoot.plugins.plushies.FurnitureUtil.isFurniture
import co.akoot.plugins.plushies.FurnitureUtil.isSeat
import co.akoot.plugins.plushies.Plushies.Companion.key
import co.akoot.plugins.plushies.util.Util.getBlockPDC
import co.akoot.plugins.plushies.util.spawnItemDisplay
import dev.geco.gsit.api.event.PreEntitySitEvent
import io.papermc.paper.datacomponent.item.ResolvableProfile
import net.kyori.adventure.key.Key
import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.block.Skull
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Transformation
import org.joml.AxisAngle4f
import org.joml.Vector3f

class Furniture : Listener {

    @EventHandler // GSit
    fun PreEntitySitEvent.cancelSit() { isCancelled = block.location.isSeat == false }

    @EventHandler
    fun BlockPlaceEvent.placeFurniture() {
        if (isCancelled || itemInHand.isFurniture.not()) return

        spawnItemDisplay(block.location, itemInHand, Transformation(
            Vector3f(),
            AxisAngle4f(),
            Vector3f(1f,1f,1f),
            AxisAngle4f()
        ))

        block.chunk.setPDC(getBlockPDC(block.location, "furniture.seat"), itemInHand.isSeat) // true or false for GSit event

        runLater(2) { when (itemInHand.furnitureHitBox) {
            "half" -> createHalfBlock(blockPlaced)
            "full" -> {} // soon
        } }
    }
}


object FurnitureUtil {
    val furnHitbox = key("furniture.hitbox")
    val seatKey = key("furniture.seat")

    val ItemStack.isSeat: Boolean
        get() = itemMeta?.hasPDC(seatKey) == true

    val Location.isSeat: Boolean?
        get() = chunk.getPDC<Boolean>(getBlockPDC(this, "furniture.seat"))

    val Block.isFurniture get() = location.isSeat != null

    val ItemStack.isFurniture: Boolean
        get() = itemMeta?.run { hasPDC(furnHitbox) || hasPDC(seatKey) } == true

    val ItemStack.furnitureHitBox: String?
        get() = itemMeta?.getPDC(furnHitbox)

    fun createHalfBlock(block: Block) {
        val state = block.state as? Skull ?: return

        val profile = ResolvableProfile.resolvableProfile()
            .skinPatch(
                ResolvableProfile.SkinPatch.skinPatch()
                    .body(Key.key("block/beetroots_stage0"))
                    .build()
            ).build()

        state.setProfile(profile)
        state.update(true)
    }
}