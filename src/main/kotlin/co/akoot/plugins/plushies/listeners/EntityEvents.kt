package co.akoot.plugins.plushies.listeners

import co.akoot.plugins.bluefox.api.FoxPlugin
import co.akoot.plugins.bluefox.api.Kolor
import co.akoot.plugins.bluefox.extensions.getPDC
import co.akoot.plugins.bluefox.extensions.hasPDC
import co.akoot.plugins.bluefox.extensions.removePDC
import co.akoot.plugins.bluefox.extensions.setPDC
import co.akoot.plugins.bluefox.util.Text
import co.akoot.plugins.bluefox.util.Text.Companion.asString
import co.akoot.plugins.bluefox.util.Text.Companion.now
import co.akoot.plugins.plushies.Plushies.Companion.key
import co.akoot.plugins.plushies.events.ModifyMerchantEvent
import co.akoot.plugins.plushies.listeners.handlers.dropHead
import co.akoot.plugins.plushies.listeners.handlers.petNeglect
import co.akoot.plugins.plushies.listeners.tasks.Golf
import co.akoot.plugins.plushies.listeners.tasks.Golf.Companion.golfKey
import co.akoot.plugins.plushies.listeners.tasks.Golf.Companion.golfSwing
import co.akoot.plugins.plushies.listeners.tasks.Throwable.Companion.axeKey
import co.akoot.plugins.plushies.util.Items.customItems
import co.akoot.plugins.plushies.util.Items.hitSound
import co.akoot.plugins.plushies.util.Items.isPlushie
import io.papermc.paper.world.MoonPhase
import org.bukkit.Material
import org.bukkit.entity.*
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.entity.EntitySpawnEvent
import org.bukkit.event.entity.EntityTargetEvent
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import kotlin.random.Random

class EntityEvents(private val plugin: FoxPlugin) : Listener {

    @EventHandler
    fun EntityTargetEvent.cancelDogAggro() {
        val dog = entity as? Wolf ?: return
        if (!dog.hasPDC(key("friendly"))) return
        isCancelled = (target is Player || (target is Wolf && (target as Wolf).isTamed))
    }

    @EventHandler
    fun projectileHit(event: ProjectileHitEvent) {
        val projectile = event.entity

        if (projectile is Snowball) {
            projectile.getPDC<Double>(axeKey)?.let {
                (event.hitEntity as? LivingEntity)?.damage(it, projectile.shooter as Player)
                projectile.remove()
            }
        }
    }

    @EventHandler
    fun entityDamage(event: EntityDamageByEntityEvent) {
        val target = event.entity
        val attacker = event.damager
        if (attacker is Player) {

            attacker.inventory.itemInMainHand.hitSound?.let { sound ->
                target.world.playSound(target.location, sound, 1.0f, 1.0f)
            }

            if (target is ArmorStand && target.hasPDC(golfKey)) {
                event.damage = 0.0
                if (golfSwing(target, attacker)) {
                    Golf(attacker, target, target.location).runTaskTimer(plugin, 20, 20)
                }
            }
        }
    }

    @EventHandler
    fun EntityDeathEvent.onEntityDeath() {
        val damageEvent = entity.lastDamageCause as? EntityDamageByEntityEvent ?: return
        val killer = damageEvent.damager

        val chance = when {
            entity is Player -> if (entity.world.moonPhase == MoonPhase.FULL_MOON) 1.0 else 0.14
            entity is Enderman -> if (entity.world.name == "world_the_end") 0.005 else 0.14
            entity.type in listOf(EntityType.WANDERING_TRADER, EntityType.WARDEN, EntityType.HAPPY_GHAST) -> 1.0
            else -> 0.08
        }

        if ((killer is Creeper && killer.isPowered) || (killer is Player && Random.nextDouble() < chance)) {
            return dropHead(killer, entity, this)
        }

        // music disc drops
        if (entity is Creeper) {
            val disc = drops.find { it.type.isRecord }
            if (disc != null && Random.nextDouble() < 0.51) {
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
        val item = event.player.inventory.itemInMainHand

        if (item.type == Material.NAME_TAG) {
            val silent = when (item.itemMeta?.customName()?.asString()?.lowercase()) {
                "silence me", "silent", "mute" -> true
                "yap", "unmute" -> false
                else -> return
            }
            event.rightClicked.isSilent = silent
            event.isCancelled = true
            return
        }

        when (val entity = event.rightClicked) {
            // anti villager lag fix
            is Villager -> entity.isAware = true
            is Tameable -> {
                // pet neglect
                if (item.type == Material.BLAZE_ROD) return petNeglect(event.player, item, entity, event)
                // friendly dog
                if (entity.type == EntityType.WOLF && entity.isTamed && entity.owner == event.player) {
                    when {
                        item.isPlushie -> {
                            // dog wont stop attacking if made friendly mid fight. set false just incase
                            (entity as Wolf).isAngry = false
                            entity.setPDC(key("friendly"), true)
                        }
                        item.type == Material.BLAZE_POWDER -> entity.removePDC(key("friendly"))
                        else -> return
                    }
                    event.isCancelled = true
                    Text(event.player) {
                        Kolor.MONTH(entity.name) + Kolor.TEXT(" is ${entity.hasPDC(key("friendly")).now} friendly!")
                    }
                }
            }
        }
    }

    @EventHandler
    fun EntitySpawnEvent.onTraderSpawn() {
        val trader = entity as? WanderingTrader ?: return
        ModifyMerchantEvent(trader).fire() ?: return
    }
}