package co.akoot.plugins.plushies.coolstuff

import co.akoot.plugins.bluefox.util.Text
import co.akoot.plugins.plushies.util.builders.CraftRecipe
import co.akoot.plugins.plushies.util.fullBoxOf
import com.destroystokyo.paper.event.player.PlayerElytraBoostEvent
import com.destroystokyo.paper.event.player.PlayerLaunchProjectileEvent
import io.papermc.paper.datacomponent.DataComponentTypes
import io.papermc.paper.datacomponent.item.CustomModelData
import io.papermc.paper.datacomponent.item.Fireworks
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.RecipeChoice.ExactChoice

class EverlastRocket() : Listener {

    init {
        for (dur in 1..3) {
            CraftRecipe.builder(
                "everlastingfirework.$dur",
                ItemStack(Material.FIREWORK_ROCKET).makeEverlastingRocket(dur)
            )
                .ingredient(ExactChoice(fullBoxOf(Material.GUNPOWDER)), dur)
                .ingredient(ExactChoice(fullBoxOf(Material.PAPER) + fullBoxOf(Material.SUGAR_CANE)))
                .shapeless()
        }
    }

    @EventHandler
    fun PlayerLaunchProjectileEvent.rocket() { setShouldConsume(!itemStack.consumeEverLastRocket()) }

    @EventHandler
    fun PlayerElytraBoostEvent.rocket() { setShouldConsume(!itemStack.consumeEverLastRocket()) }
}

fun ItemStack.consumeEverLastRocket(): Boolean {
    val maxDamage = getData(DataComponentTypes.MAX_DAMAGE) ?: return false
    val damage = getData(DataComponentTypes.DAMAGE) ?: 0
    if (damage >= maxDamage - 1) return false

    setData(DataComponentTypes.DAMAGE, damage + 1)
    return true
}

fun ItemStack.makeEverlastingRocket(duration: Int) = apply {
    setData(DataComponentTypes.ITEM_NAME, Text("Everlasting Rocket").component)
    setData(DataComponentTypes.CUSTOM_MODEL_DATA, CustomModelData.customModelData().addString("el.rocket.${duration}").build())
    setData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true)
    setData(DataComponentTypes.MAX_STACK_SIZE, 1)
    setData(DataComponentTypes.MAX_DAMAGE, 5184)
    setData(
        DataComponentTypes.FIREWORKS,
        Fireworks.fireworks().flightDuration(duration)
    )
}