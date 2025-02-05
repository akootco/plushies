package co.akoot.plugins.plushies.util

import org.bukkit.entity.*

object MobHead {
    // erm, what did i get myself into
    val headTexture: (Entity) -> String = { victim ->
        // TODO: i need to get ALL of the variants and colors smH@!!!
        when (victim) {
            is TraderLlama -> "trader_llama.${victim.color.name}"
            is MushroomCow -> "mooshroom.${victim.variant.name}"
            is Horse -> "horse.${victim.color.name}_${victim.style.name}"
            is Villager -> "villager.${victim.profession.key.key}_${victim.villagerType.key.key}"
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
            is Fox -> "fox.${victim.foxType.name}${if (victim.isSleeping) "_sleeping" else ""}"

            else -> "default.${victim.type.name}"
        }
    }
}