package co.akoot.plugins.plushies.listeners.handlers

import co.akoot.plugins.bluefox.extensions.hasMeta
import co.akoot.plugins.bluefox.extensions.setMeta
import co.akoot.plugins.bluefox.util.Text
import co.akoot.plugins.plushies.Plushies.Companion.headConf
import co.akoot.plugins.plushies.util.builders.ItemBuilder
import me.arcaniax.hdb.api.HeadDatabaseAPI
import org.bukkit.Material
import org.bukkit.entity.*
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack

val headTexture: (Entity) -> String = { victim ->
    // TODO: i need to get ALL of the variants and colors smH@!!!
    when (victim) {
        is TraderLlama -> "trader_llama.${victim.color.name}"
        is MushroomCow -> "mooshroom.${victim.variant.name}"
        is Horse -> "horse.${victim.color.name}_${victim.style.name}"
        is Villager -> "villager.${victim.villagerType.key.key}_${victim.profession.key.key}"
        is ZombieVillager -> "zombie_villager.${victim.villagerType.key.key}_${victim.villagerProfession.key.key}"
        is Parrot -> "parrot.${victim.variant.name}"
        is Panda -> "panda.${victim.combinedGene}"
        is Rabbit -> "rabbit.${victim.rabbitType.name}"
        is Frog -> "frog.${victim.variant.key.key}"
        is Wolf -> "wolf.${victim.variant.key.key}"
        is Cat -> "cat.${victim.catType.key.key}"
        is Sheep -> "sheep.${if (victim.name == "jeb_") "rainbow" else victim.color?.name}"
        is Creeper -> "default.${if (victim.isPowered) "charged_creeper" else null}"
        is Strider -> "strider.${if (victim.isShivering) "cold" else "normal"}"
        is Ghast -> "ghast.${if (victim.isCharging) "crying" else "normal"}"
        is Llama -> "llama.${victim.color.name}"
        is Axolotl -> "axolotl.${victim.variant.name}"
        is Fox -> "fox.${victim.foxType.name}${if (victim.isSleeping) "_sleeping" else null}"
        // 1.21.5+
//            is Cow -> "cow.${victim.variant.key.key}"
        is HappyGhast -> { // what the heck is this all about?
            val color = victim.equipment?.getItem(EquipmentSlot.BODY)?.type?.name?.substringBeforeLast("_")
                ?.lowercase().takeIf { it != "air" } ?: "default"
            "happy_ghast.$color"
        }
        is Chicken -> "chicken.${victim.variant.key.key}"
        is Pig -> "pig.${victim.variant.key.key}"

        else -> victim.type.name
    }
}

fun dropHead(killer: Entity, victim: Entity, event: EntityDeathEvent) {
    if (killer is Creeper) {
        if (killer.hasMeta("dropped")) return
        killer.setMeta("dropped", true)
    }


    when(victim) {
        is Player -> {
            event.drops.add(
                ItemBuilder.builder(ItemStack(Material.PLAYER_HEAD))
                    .playerHead(victim)
                    .build()
            )
            return
        }
        is Creeper -> if (victim.isPowered) event.drops.removeLast()
    }

    event.drops.add(
        headConf.getString(headTexture(victim).lowercase())
            ?.takeIf { it.isNotBlank() }?.let {
                val head = HeadDatabaseAPI().getItemHead(it)

                ItemBuilder.builder(head ?: ItemStack(Material.PLAYER_HEAD))
                    .apply { if (head == null) headTexture(it) }
                    .headSound("entity.${victim.type.name.lowercase()}.ambient")
                    .itemName(Text("${victim.name} Head").component)
                    .build()
            }
    )
//    println(headTexture(victim).lowercase())
}