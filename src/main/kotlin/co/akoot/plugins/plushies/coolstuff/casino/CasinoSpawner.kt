package co.akoot.plugins.plushies.coolstuff.casino

import co.akoot.plugins.bluefox.api.dialog
import co.akoot.plugins.bluefox.extensions.setPDC
import co.akoot.plugins.bluefox.util.text
import co.akoot.plugins.plushies.Plushies.Companion.key
import co.akoot.plugins.plushies.api.Interactable
import co.akoot.plugins.plushies.coolstuff.casino.util.Casino
import co.akoot.plugins.plushies.coolstuff.casino.util.CasinoGame
import co.akoot.plugins.plushies.util.builders.ItemBuilder
import io.papermc.paper.datacomponent.item.ResolvableProfile
import io.papermc.paper.dialog.Dialog
import net.kyori.adventure.key.Key
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.entity.Mannequin
import org.bukkit.event.player.PlayerInteractEvent

object CasinoSpawner : Interactable {
    override val key = key("casino")

    override val item = ItemBuilder.builder(Material.KNOWLEDGE_BOOK)
        .itemName("Casino Spawner".text)
        .itemModel("villager_spawn_egg")
        .pdc(key)
        .glint()
        .build()

    override fun place(event: PlayerInteractEvent): Boolean {
        if (event.blockFace != BlockFace.UP) return false

        val block = event.clickedBlock ?: return false
        val above = block.getRelative(BlockFace.UP)
        val above2 = block.getRelative(BlockFace.UP, 2)

        if (!above.isEmpty || !above2.isEmpty) return false

        event.player.showDialog(dealerPicker(above.location))
        return true
    }

    private fun dealerPicker(
        location: Location,
    ): Dialog = dialog {
        title("Choose Game".text)
        columns(2)

        Casino.all().forEach { game ->
            button(150, game.key.key.text) { p, _ ->
                spawnDealer(location, p.yaw, game)
            }
        }
    }

    private fun spawnDealer(location: Location, yaw: Float, game: CasinoGame) {
        location.world.spawn(location.clone().add(0.5, 0.0, 0.5), Mannequin::class.java) {
            it.setRotation(yaw + 180f, 0f)
            it.isImmovable = true
            it.isInvulnerable = true
            it.setAI(false)
            it.customName(game.displayName.text)
            it.description = null

            it.profile = ResolvableProfile.resolvableProfile()
                .skinPatch { skin ->
                    skin.body(
                        Key.key("casino:entity/dealers/dealer_1")
                    )
                }
                .build()

            it.setPDC(game.key, true)
        }
    }
}