package co.akoot.plugins.plushies.coolstuff.atm

import co.akoot.plugins.bluefox.util.text
import co.akoot.plugins.plushies.Plushies
import co.akoot.plugins.plushies.api.Interactable
import co.akoot.plugins.plushies.api.owns
import co.akoot.plugins.plushies.util.builders.ItemBuilder
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.entity.Entity
import org.bukkit.entity.Interaction
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerInteractEvent
import org.joml.Vector3f

object ATM : Interactable {
    override val key = Plushies.key("atm")
    override val height = 2.001f
    override val width = 1.001f
    override val translation = Vector3f(0f, 0.5f, 0f)
    override val setOwner = true
    override val cancelPlacement = true
    override val removable = true
    override val rotatable = true

    override val placeSound = "block.iron.place"
    override val breakSound= "block.iron.break"

    override val item = ItemBuilder.builder(Material.KNOWLEDGE_BOOK)
        .itemName("ATM".text)
        .pdc(key)
        .itemModel(key.toString())
        .build()
        .clone()

    override fun interact(entity: Interaction, player: Player) {
        if (player.isSneaking && player.owns(entity))  {
            player.showDialog(ATMMenu.settingsMenu(entity))
            return
        }

        player.swingMainHand()
        player.showDialog(ATMMenu.mainMenu(entity))
    }

    override fun place(event: PlayerInteractEvent): Boolean {
        if (event.blockFace != BlockFace.UP) return false

        val block = event.clickedBlock ?: return false
        val above = block.getRelative(BlockFace.UP)
        val above2 = block.getRelative(BlockFace.UP, 2)

        if (!above.isEmpty || !above2.isEmpty) return false
        if (!super.place(event)) return false

        above.type = Material.BARRIER
        above2.type = Material.BARRIER

        return true
    }

    override fun remove(entity: Entity, damager: Entity?): Boolean {
        if (damager !is Player || !damager.isSneaking) return false
        if (!super.remove(entity, damager)) return false

        val block = entity.location.block
        block.getRelative(BlockFace.UP).type = Material.AIR
        block.type = Material.AIR

        return true
    }
}