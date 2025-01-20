package co.akoot.plugins.plushies.util

import org.bukkit.entity.*

object MobHead {
    // erm, what did i get myself into
    val headTexture: (Entity) -> String = { victim ->
        // TODO: i need to get ALL of the variants and colors smH@!!!
        when (victim) {
            is MushroomCow -> "mooshroom.${victim.variant.name}"
            is Horse -> "horse.${victim.color.name}.${victim.style.name}"
            is Villager -> "villager.${victim.profession.key.key}.${victim.villagerType.key.key}"
            is ZombieVillager -> "zombie_villager.${victim.villagerProfession.key.key}.${victim.villagerType.key.key}"
            is Parrot -> "parrot.${victim.variant.name}"
            is Panda -> "panda.${victim.mainGene.name}"
            is Rabbit -> "rabbit.${victim.rabbitType.name}"
            is Frog -> "frog.${victim.variant.key.key}"
            is Wolf -> "wolf.${victim.variant.key.key}"
            is Cat -> "cat.${victim.catType.key.key}"
            is Sheep -> "sheep.${if (victim.customName()?.equals("jeb_") == true) "rainbow" else victim.color?.name}"
            is Creeper -> "creeper.${if (victim.isPowered) "charged" else "normal"}"
            is Strider -> "strider.${if (victim.isShivering) "cold" else "normal"}"
            is Ghast -> "ghast.${if (victim.isCharging) "crying" else "normal"}"
            is Llama -> "llama.${victim.color.name}"
            is Axolotl -> "axolotl.${victim.variant.name}"

            else -> "default.${victim.type.name}"
        }
    }
}