package co.akoot.plugins.plushies.coolstuff

import co.akoot.plugins.bluefox.api.dialog
import co.akoot.plugins.bluefox.extensions.getMeta
import co.akoot.plugins.bluefox.extensions.setMeta
import co.akoot.plugins.bluefox.extensions.withLore
import co.akoot.plugins.bluefox.util.*
import co.akoot.plugins.bluefox.util.Text.Companion.plus
import co.akoot.plugins.plushies.Plushies.Companion.key
import co.akoot.plugins.plushies.api.Interactable
import co.akoot.plugins.plushies.util.builders.ItemBuilder
import io.papermc.paper.datacomponent.DataComponentTypes
import io.papermc.paper.datacomponent.item.LodestoneTracker
import io.papermc.paper.dialog.Dialog
import io.papermc.paper.registry.RegistryAccess
import io.papermc.paper.registry.RegistryKey
import net.kyori.adventure.text.Component
import net.kyori.adventure.translation.Translatable
import org.bukkit.Material
import org.bukkit.block.Biome
import org.bukkit.entity.Player

object BiomeCompass : Interactable {
    override val key = key("biomefinder")
    override val placeable = false

    override val item = ItemBuilder.builder(Material.KNOWLEDGE_BOOK)
        .itemName("Nature's Compass".text)
        .pdc(key)
        .stackSize(1)
        .itemModel(key.toString())
        .build()
        .clone()

    override fun interact(player: Player) {
        openBiomeFinder(player)
    }
}

var Player.compassCooldown
    get() = getMeta<Long>("bf.cooldown") ?: 0
    set(value) = setMeta("bf.cooldown", value)

val Translatable.component: Component
    get() = Component.translatable(translationKey())


val biomes = RegistryAccess.registryAccess()
    .getRegistry(RegistryKey.BIOME)

private fun biomeFinder(): Dialog = dialog {
    closeWithEscape(true)
    title("Biome Finder".text)
    columns(2)

    for (biome in biomes.sortedBy { it.key.value() }) {
        button(150, biome.component) { p, _ ->
            findBiome(p, biome)
        }
    }
}

private fun findBiome(player: Player, biome: Biome) {
    val playerLocation = player.location.clone()
    val compass = BiomeCompass.item

    player.inventory.itemInMainHand.amount--
    player.sendActionBar("Searching for biome...".text)

    async {
        val nearest = player.world.locateNearestBiome(
            playerLocation,
            10000,
            64,
            64,
            biome
        )

        if (nearest == null) {
            sync {
                player.give(compass)
                player.sendActionBar {
                    error("Could not find ", biome.component, "!").zip
                }
            }
            return@async
        }

        val distance = nearest.location.distance(playerLocation).toInt()

        sync {
            player.compassCooldown = System.currentTimeMillis() + TimeUtil.parseTime("2m")

            player.sendActionBar {
                text(biome.component + " is $distance blocks away.").zip
            }

            val compass = compass.apply {
                withLore(
                    quote("Pointing to nearest ".text + biome.component).zip,
                    nearest.location.toComponent()
                )

                setData(
                    DataComponentTypes.LODESTONE_TRACKER,
                    LodestoneTracker.lodestoneTracker()
                        .location(nearest.location)
                        .tracked(false) // ya ig
                        .build()
                )

                unsetData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE)
            }

            player.give(compass)
        }
    }
}

private fun openBiomeFinder(player: Player) {
    val now = System.currentTimeMillis()
    val cooldown = player.compassCooldown

    if (now < cooldown) {
        player.sendActionBar(error(TimeUtil.getTimeString(cooldown - now), " remaining."))
        return
    }

    player.showDialog(biomeFinder())
}