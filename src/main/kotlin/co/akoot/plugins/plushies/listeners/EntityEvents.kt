package co.akoot.plugins.plushies.listeners

import co.akoot.plugins.bluefox.api.FoxPlugin
import co.akoot.plugins.bluefox.extensions.getPDC
import co.akoot.plugins.bluefox.util.Text
import co.akoot.plugins.plushies.Plushies.Companion.headConf
import co.akoot.plugins.plushies.listeners.tasks.Golf
import co.akoot.plugins.plushies.listeners.tasks.Golf.Companion.golfKey
import co.akoot.plugins.plushies.listeners.tasks.Golf.Companion.golfSwing
import co.akoot.plugins.plushies.listeners.tasks.Throwable.Companion.axeKey
import co.akoot.plugins.plushies.util.MobHead.headTexture
import co.akoot.plugins.plushies.util.builders.ItemBuilder
import net.kyori.adventure.text.logger.slf4j.ComponentLogger.logger
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.Sound
import org.bukkit.entity.*
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.event.player.PlayerArmorStandManipulateEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import java.util.regex.Pattern

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
    fun onEntityDeath(event: EntityDeathEvent) {
        val victim = event.entity
        val damageEvent = victim.lastDamageCause as? EntityDamageByEntityEvent ?: return
        val killer = damageEvent.damager

        if (killer is Creeper && killer.isPowered) {
            if (killer.persistentDataContainer.has(NamespacedKey("plushies", "dropped"))) return

            if (victim is Player) {
                event.drops.add(
                    ItemBuilder.builder(ItemStack(Material.PLAYER_HEAD))
                        .playerHead(victim)
                        .build()
                )
                return
            } else {
                if (victim is Creeper && victim.isPowered) event.drops.removeLast()

                event.drops.add(
                    headConf.getString(headTexture(victim).lowercase())?.let {
                        ItemBuilder.builder(ItemStack(Material.PLAYER_HEAD))
                            .headTexture(it)
                            .itemName(Text("${victim.name} Head").component)
                            .build()
                    }
                )
            }

            logger().info(headTexture(victim).lowercase()) // for testing
            // set pdc so the creeper only gives 1 head
            killer.persistentDataContainer.set(NamespacedKey("plushies", "dropped"), PersistentDataType.BOOLEAN, true)
        }
    }

    @EventHandler
    fun interactEntity(event: PlayerInteractEntityEvent) {
        val player = event.player
        val pItem = player.inventory.itemInMainHand
        val entity = event.rightClicked

        if (event.isCancelled) return

        if (player.isSneaking) {
            if (pItem.isEmpty) {
                if (entity is Pig && entity.getPassengers().isEmpty() && entity.hasSaddle()) {
                    entity.setSaddle(false)
                    entity.world.dropItem(entity.location.add(0.0, 1.0, 0.0), ItemStack(Material.SADDLE))
                    entity.world.playSound(entity.location, Sound.ENTITY_PIG_SADDLE, 0.5f, 1.0f)
                    return
                } else if (entity is Strider && entity.getPassengers().isEmpty() && entity.hasSaddle()) {
                    entity.setSaddle(false)
                    entity.world.dropItem(entity.location.add(0.0, 1.8, 0.0), ItemStack(Material.SADDLE))
                    entity.world.playSound(entity.location, Sound.ENTITY_STRIDER_SADDLE, 0.5f, 1.0f)
                    return
                }
            } else if (pItem.type == Material.BLAZE_ROD && entity is Tameable) {
                petNeglect(player, pItem, entity, event)
                return
            }
        }
    }

    private fun petNeglect(player: Player, pItem: ItemStack, entity: Tameable, event: PlayerInteractEntityEvent) {
        val meta = pItem.itemMeta ?: return
        val displayName = meta.displayName().toString()

        if (!Pattern.compile("be\\s*free", Pattern.CASE_INSENSITIVE).matcher(displayName).matches()) return

        if (entity.owner?.uniqueId != player.uniqueId) {
            player.sendMessage(
                (Text("${entity.name} belongs to ") +
                        Text(entity.owner?.name.toString(), "player")
                            .execute("/profile ${entity.owner?.name}")).component
            )
            return
        }

        entity.isTamed = false
        event.isCancelled = true
        player.sendMessage((Text(entity.name, "accent") + Text(" is no longer tamed!", "text")).component)
    }
}