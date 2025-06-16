package co.akoot.plugins.plushies.listeners.handlers

import co.akoot.plugins.bluefox.extensions.hasMeta
import co.akoot.plugins.bluefox.extensions.setMeta
import co.akoot.plugins.bluefox.util.Text
import co.akoot.plugins.plushies.Plushies.Companion.headConf
import co.akoot.plugins.plushies.util.MobHead.headTexture
import co.akoot.plugins.plushies.util.builders.ItemBuilder
import org.bukkit.Material
import org.bukkit.entity.Creeper
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.inventory.ItemStack

fun dropHead(killer: Entity, victim: Entity, event: EntityDeathEvent) {
    if (killer.hasMeta("dropped")) return

    if (victim is Player) {
        event.drops.add(
            ItemBuilder.builder(ItemStack(Material.PLAYER_HEAD))
                .playerHead(victim)
                .build())
    } else {
        if (victim is Creeper && victim.isPowered) event.drops.removeLast()

        event.drops.add(
            headConf.getString(headTexture(victim).lowercase())
                ?.takeIf { it.isNotBlank() }?.let {
                ItemBuilder.builder(ItemStack(Material.PLAYER_HEAD))
                    .headTexture(it)
                    .headSound("entity.${victim.type.name.lowercase()}.ambient")
                    .itemName(Text("${victim.name} Head").component)
                    .build()
            }
        )
    }

//    logger().info(headTexture(victim).lowercase()) // for testing
    // set meta so the creeper only gives 1 head
    killer.setMeta("dropped", true)
}