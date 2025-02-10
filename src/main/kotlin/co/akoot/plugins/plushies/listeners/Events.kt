package co.akoot.plugins.plushies.listeners

import co.akoot.plugins.plushies.listeners.tasks.Throwable.Companion.axeKey
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.entity.Snowball
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.persistence.PersistentDataType

class Events: Listener {
    @EventHandler
    fun projectileHit(event: ProjectileHitEvent) {
        val projectile = event.entity
        val target = event.hitEntity as LivingEntity
        val pdc = projectile.persistentDataContainer

       if (projectile is Snowball && pdc.has(axeKey))  {
           pdc.get(axeKey, PersistentDataType.DOUBLE)?.let { target.damage(it, projectile.shooter as Player) }
       }
    }
}