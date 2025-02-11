package co.akoot.plugins.plushies.listeners

import co.akoot.plugins.bluefox.extensions.getPDC
import co.akoot.plugins.plushies.listeners.tasks.Throwable.Companion.axeKey
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.entity.Snowball
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.ProjectileHitEvent

class Events: Listener {
    @EventHandler
    fun projectileHit(event: ProjectileHitEvent) {
        val projectile = event.entity

       if (projectile is Snowball)  {
           projectile.getPDC<Double>(axeKey)?.let {
               (event.hitEntity as? LivingEntity)?.damage(it, projectile.shooter as Player)
           }
       }
    }
}