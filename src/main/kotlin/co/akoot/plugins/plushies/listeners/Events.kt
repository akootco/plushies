package co.akoot.plugins.plushies.listeners

import co.akoot.plugins.bluefox.extensions.getMeta
import co.akoot.plugins.bluefox.extensions.getPDC
import co.akoot.plugins.bluefox.extensions.hasPDC
import co.akoot.plugins.plushies.Plushies.Companion.key
import co.akoot.plugins.plushies.listeners.handlers.dropItem
import co.akoot.plugins.plushies.listeners.handlers.plushieFrame
import co.akoot.plugins.plushies.listeners.tasks.Golf.Companion.golfKey
import co.akoot.plugins.plushies.listeners.tasks.Golf.Companion.spawnGolfBall
import com.destroystokyo.paper.event.block.BeaconEffectEvent
import io.papermc.paper.event.player.PlayerItemFrameChangeEvent
import io.papermc.paper.event.player.PlayerItemFrameChangeEvent.ItemFrameChangeAction
import org.bukkit.entity.ItemFrame
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.hanging.HangingBreakEvent
import org.bukkit.event.hanging.HangingPlaceEvent
import org.bukkit.event.player.PlayerArmorStandManipulateEvent
import org.bukkit.potion.PotionEffectType

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

    @EventHandler
    fun BeaconEffectEvent.beaconEffect() {
        isCancelled = effect.type == PotionEffectType.JUMP_BOOST && player.hasPDC(key("plsnojump"))
        effect = effect.withDuration(effect.duration * 2)
    }
}