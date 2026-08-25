package co.akoot.plugins.plushies.listeners

import co.akoot.plugins.plushies.api.Interactables
import org.bukkit.entity.Interaction
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPistonExtendEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerInteractAtEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.util.BoundingBox

class InteractionListener : Listener {

    @EventHandler
    fun PlayerInteractEvent.place() {
        if (hand != EquipmentSlot.HAND) return
        if (!action.isRightClick) return

        val interactable = Interactables.find(item ?: return) ?: return

        if (!interactable.placeable) {
            interactable.interact(player)
            isCancelled = true
            return
        }

        if (isCancelled) return

        isCancelled = interactable.cancelPlacement
        interactable.place(this)
    }

    @EventHandler
    fun PlayerInteractAtEntityEvent.interaction() {
        if (isCancelled) return
        if (hand != EquipmentSlot.HAND) return // dumb
        val interaction = rightClicked as? Interaction ?: return
        val interactable = Interactables.find(interaction) ?: return

        interactable.interact(interaction, player)
    }

    @EventHandler
    fun EntityDamageByEntityEvent.remove() {
        if (isCancelled) return
        val interaction = entity as? Interaction ?: return
        if (interaction.passengers.isNotEmpty()) return

        val interactable = Interactables.find(interaction) ?: return
        if (!interactable.removable) return

        interactable.remove(interaction, damager)
    }

    @EventHandler
    fun BlockPistonExtendEvent.pistonBreak() {
        if (isCancelled) return

        (blocks.map { it.getRelative(direction) } + block.getRelative(direction))
            .forEach { destination -> // sick of ts
                destination.world
                    .getNearbyEntities(BoundingBox.of(destination))
                    .filterIsInstance<Interaction>()
                    .forEach { interaction ->
                        val interactable = Interactables.find(interaction) ?: return@forEach

                        if (!interactable.pushable) {
                            isCancelled = true
                            return@forEach
                        }

                        if (interactable.removable) {
                            interactable.remove(interaction)
                        }
                    }
            }
    }
}