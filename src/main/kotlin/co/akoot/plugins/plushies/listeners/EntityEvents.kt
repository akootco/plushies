package co.akoot.plugins.plushies.listeners

import co.akoot.plugins.bluefox.api.FoxPlugin
import co.akoot.plugins.bluefox.extensions.getPDC
import co.akoot.plugins.plushies.listeners.handlers.dropHead
import co.akoot.plugins.plushies.listeners.handlers.petNeglect
import co.akoot.plugins.plushies.listeners.tasks.Golf
import co.akoot.plugins.plushies.listeners.tasks.Golf.Companion.golfKey
import co.akoot.plugins.plushies.listeners.tasks.Golf.Companion.golfSwing
import co.akoot.plugins.plushies.listeners.tasks.Throwable.Companion.axeKey
import co.akoot.plugins.plushies.util.Items.customItems
import org.bukkit.entity.*
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.event.player.PlayerArmorStandManipulateEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import kotlin.random.Random

class EntityEvents(private val plugin: FoxPlugin) : Listener {

    @EventHandler
    fun projectileHit(event: ProjectileHitEvent) {
        val projectile = event.entity

        if (projectile is Snowball) {
            projectile.getPDC<Double>(axeKey)?.let {
                (event.hitEntity as? LivingEntity)?.damage(it, projectile.shooter as Player)
            }
        }
    }

    @EventHandler
    fun entityDamage(event: EntityDamageByEntityEvent) {
        val target = event.entity
        val attacker = event.damager

        if (target is ArmorStand && attacker is Player) {
            if (target.getPDC<String>(golfKey) != null) {
                event.isCancelled = true // cancel the event so the armor stand isnt destroyed
                if (golfSwing(target, attacker)) {
                    Golf(attacker, target, target.location).runTaskTimer(plugin, 20, 20)
                }
            }
        }
    }

    @EventHandler
    fun armorStandEdit(event: PlayerArmorStandManipulateEvent) {
        val clicked = event.rightClicked
        if (clicked.getPDC<String>(golfKey) != null) {
            clicked.remove()
        }
    }

    @EventHandler
    fun EntityDeathEvent.onEntityDeath() {
        val damageEvent = entity.lastDamageCause as? EntityDamageByEntityEvent ?: return
        val killer = damageEvent.damager

        if (killer is Creeper && killer.isPowered) {
            return dropHead(killer, entity, this)
        }

        // music disc drops
        if (entity is Creeper) {
            val disc = drops.find { it.type.isRecord }
            // 50% chance to drop custom disc
            if (disc != null && Random.nextDouble() < 0.2) {
                drops.apply {
                    remove(disc)
                    add(customItems.filter { it.value.type.isRecord }
                        .values.random().clone())
                }
                return
            }
        }
    }

    @EventHandler
    fun interactEntity(event: PlayerInteractEntityEvent) {
        if (event.isCancelled) return

        val player = event.player
        val item = player.inventory.itemInMainHand

        when (val entity = event.rightClicked) {
            is Tameable -> {
                petNeglect(player, item, entity, event)
            }
        }
    }
}