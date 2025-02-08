package co.akoot.plugins.plushies.listeners

import co.akoot.plugins.bluefox.util.Text
import co.akoot.plugins.plushies.Plushies.Configs.headConf
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
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import java.util.regex.Pattern

class EntityEvents : Listener {

@EventHandler
fun onEntityDeath(event: EntityDeathEvent) {
    val victim = event.entity
    val damageEvent = victim.lastDamageCause as? EntityDamageByEntityEvent ?: return
    val killer = damageEvent.damager

    if (killer is Creeper && killer.isPowered) {
        if (killer.persistentDataContainer.has(NamespacedKey("plushies", "dropped"))) return

        if (victim is Player) {
            event.drops.add(ItemBuilder.builder(ItemStack(Material.PLAYER_HEAD))
                .playerHead(victim)
                .build())
            return
        }
        else {
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
            }
            else if (pItem.type == Material.BLAZE_ROD && entity is Tameable) {
                petNeglect(player, pItem, entity, event)
                return
            }
        }
    }

    private fun petNeglect(player: Player, pItem: ItemStack, entity: Tameable, event: PlayerInteractEntityEvent) {
        if (!entity.isTamed) return

        val meta = pItem.itemMeta ?: return
        val displayName = meta.displayName().toString()

        if (!Pattern.compile("be\\s*free", Pattern.CASE_INSENSITIVE).matcher(displayName).matches()) return

        if (entity.owner?.uniqueId != player.uniqueId) {
            player.sendMessage(
                (Text("${entity.name} belongs to ") +
                        Text(entity.owner?.name.toString(), "player").execute("/profile ${entity.owner?.name}")).component
            )
            return
        }

        entity.isTamed = false
        event.isCancelled = true
        player.sendMessage((Text(entity.name, "accent") + Text(" is no longer tamed!", "text")).component)
    }
}