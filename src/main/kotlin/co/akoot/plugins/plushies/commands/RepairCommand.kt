package co.akoot.plugins.plushies.commands

import co.akoot.plugins.bluefox.api.CatCommand
import co.akoot.plugins.plushies.Plushies
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.meta.Damageable

class RepairCommand(plugin: Plushies) : CatCommand(plugin, "repair") {
    init {
        noargs {
            val player = getPlayerSender(it) ?: return@noargs false
            val item = player.inventory.itemInMainHand

            if (Enchantment.MENDING !in item.enchantments) return@noargs false

            item.editMeta { meta ->
                val damageable = meta as? Damageable ?: return@editMeta
                if (damageable.damage == 0) return@editMeta

                val xpNeeded = (damageable.damage + 1) / 2 // okay.
                val xpSpent = player.calculateTotalExperiencePoints().coerceAtMost(xpNeeded)

                damageable.damage = (damageable.damage - xpSpent * 2).coerceAtLeast(0)
                player.giveExp(-xpSpent)
            }

            true
        }
    }
}