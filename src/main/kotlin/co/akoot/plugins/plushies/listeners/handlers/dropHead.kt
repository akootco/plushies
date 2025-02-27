package co.akoot.plugins.plushies.listeners.handlers

import co.akoot.plugins.bluefox.util.Text
import co.akoot.plugins.plushies.Plushies.Companion.headConf
import co.akoot.plugins.plushies.util.MobHead.headTexture
import co.akoot.plugins.plushies.util.builders.ItemBuilder
import net.kyori.adventure.text.logger.slf4j.ComponentLogger.logger
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.Creeper
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

fun dropHead(killer: Entity, victim: Entity, event: EntityDeathEvent) {
    if (killer.persistentDataContainer.has(NamespacedKey("plushies", "dropped"))) return

    if (victim is Player) {
        event.drops.add(
            ItemBuilder.builder(ItemStack(Material.PLAYER_HEAD))
                .playerHead(victim)
                .build())
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