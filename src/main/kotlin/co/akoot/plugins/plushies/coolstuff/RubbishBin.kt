package co.akoot.plugins.plushies.coolstuff

import co.akoot.plugins.bluefox.extensions.getMeta
import co.akoot.plugins.bluefox.extensions.setMeta
import co.akoot.plugins.bluefox.util.text
import co.akoot.plugins.plushies.Plushies.Companion.key
import co.akoot.plugins.plushies.api.Interactable
import co.akoot.plugins.plushies.api.Storage
import co.akoot.plugins.plushies.api.openMenu
import co.akoot.plugins.plushies.util.builders.ItemBuilder
import io.papermc.paper.datacomponent.DataComponentTypes
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerInteractEvent
import org.joml.Vector3f

object RubbishBin : Interactable {

    override val key = key("rubbish_bin")
    override fun translation() = Vector3f(0f, 0.5f, 0f)
    override val removable = true
    override val rotatable = true
    override fun width() = 0.8f
    override fun height() = 1f
    override val placeSound = "block.decorated_pot.place"
    override val breakSound = "block.decorated_pot.break"
    override fun item() = ItemBuilder.builder(Material.POISONOUS_POTATO)
        .itemName("Rubbish Bin".text)
        .stackSize(1)
        .itemModel("plushies:rubbish_bin")
        .unsetData(DataComponentTypes.CONSUMABLE)
        .unsetData(DataComponentTypes.FOOD)
        .pdc(key)
        .build()

    override fun interact(entity: Entity, player: Player) {
        player.openMenu(RubbishBinGUI.get(entity))
    }

    override fun place(event: PlayerInteractEvent): Boolean {
        val clicked = event.clickedBlock ?: return false
        val above = clicked.getRelative(BlockFace.UP)

        if (event.blockFace != BlockFace.UP || above.type != Material.AIR) return false
        return super.place(event)
    }

    override fun remove(entity: Entity, damager: Entity?): Boolean {
        RubbishBinGUI.get(entity).remove()
        return super.remove(entity, damager)
    }
}

private class RubbishBinGUI(override val entity: Entity) : Storage(
    Component.text("Rubbish Bin"),
    KEY,
    StorageConfig(rows = 6),
    save = false
) {

    override val openSound = StorageSound(
        "block.copper_chest.open",
        2f
    )

    override val closeSound = StorageSound(
        "block.copper_chest.close",
        2f
    )

    companion object {
        private const val KEY = "rubbish.bin.inv"

        fun get(entity: Entity): RubbishBinGUI {
            return entity.getMeta<RubbishBinGUI>(KEY)
                ?: RubbishBinGUI(entity).also {
                    entity.setMeta(KEY, it)
                    it.loadContents()
                }
        }
    }
}