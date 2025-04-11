package co.akoot.plugins.plushies.listeners

import co.akoot.plugins.plushies.listeners.tasks.Golf.Companion.spawnGolfBall
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent

class Events : Listener {

    @EventHandler
    fun blockPLace(event: BlockPlaceEvent) {
        event.isCancelled = spawnGolfBall(event.player, event.block.location.add(0.5, 0.0, 0.5))
    }
}