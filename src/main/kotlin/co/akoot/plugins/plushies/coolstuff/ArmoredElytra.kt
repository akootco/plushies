package co.akoot.plugins.plushies.coolstuff

import co.akoot.plugins.bluefox.extensions.getPDC
import co.akoot.plugins.bluefox.extensions.removePDC
import co.akoot.plugins.bluefox.extensions.setPDC
import co.akoot.plugins.bluefox.util.parse
import co.akoot.plugins.plushies.Plushies.Companion.key
import io.papermc.paper.datacomponent.DataComponentTypes
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.Tag
import org.bukkit.entity.EntityType
import org.bukkit.entity.ExperienceOrb
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.PrepareAnvilEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import java.util.Base64

class ArmoredElytra() : Listener {
    private val key = key("armored_wings")

    val comps = setOf(
        DataComponentTypes.ATTRIBUTE_MODIFIERS,
        DataComponentTypes.ENCHANTMENTS,
        DataComponentTypes.DAMAGE_RESISTANT
    )

    var ItemStack.armoredElytra: ItemStack?
        get() = itemMeta
            ?.getPDC<String>(key)
            ?.let { ItemStack.deserializeBytes(Base64.getDecoder().decode(it)) }

        set(value) {
            if (value == null) { comps.forEach { resetData(it) } }
            editMeta {
                if (value == null) it.removePDC(key)
                else it.setPDC(key, Base64.getEncoder().encodeToString(value.serializeAsBytes()))
            }
        }

    @EventHandler
    fun PrepareAnvilEvent.combine() {
        val first = inventory.firstItem ?: return
        val second = inventory.secondItem ?: return

        if (first.type != Material.ELYTRA || first.armoredElytra != null || !Tag.ITEMS_CHEST_ARMOR.isTagged(second.type)) return

        view.repairCost = 10

        result = first.clone().apply {
            armoredElytra = second
            editMeta { it.customName(view.renameText?.run { if (isBlank()) null else parse() }) }
            copyDataFrom(second) { type -> type in comps }
        }
    }

    @EventHandler
    fun PlayerInteractEvent.split() {
        val elytra = item ?: return
        val block = clickedBlock ?: return
        if (block.type != Material.GRINDSTONE) return

        elytra.armoredElytra?.let { armor ->
            elytra.armoredElytra = null
            block.world.dropItemNaturally(block.location.toCenterLocation().add(0.0,0.7,0.0), armor)
            block.world.playSound(block.location, Sound.BLOCK_GRINDSTONE_USE, 1f, 1f)
            (block.world.spawnEntity(block.location, EntityType.EXPERIENCE_ORB) as ExperienceOrb).experience = 160
            setUseInteractedBlock(Event.Result.DENY)
        }
    }
}