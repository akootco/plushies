package co.akoot.plugins.plushies.listeners

import co.akoot.plugins.plushies.api.Interactables
import co.akoot.plugins.plushies.api.Interactables.hasInteractable
import co.akoot.plugins.plushies.api.Placement
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPistonExtendEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerInteractAtEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.util.BoundingBox

class InteractionListener : Listener {

    @EventHandler
    fun PlayerInteractEvent.place() {
        if (hand != EquipmentSlot.HAND) return

        val entity = clickedBlock?.let { block ->
            block.world.getNearbyEntities(BoundingBox.of(block))
                .firstOrNull { Interactables.find(it) != null }
        }

        val entityInteractable = entity?.let(Interactables::find)
        val itemInteractable = item?.let(Interactables::find)

        if (entityInteractable == null && itemInteractable == null) return

        if (action.isRightClick) {
            if (entityInteractable != null) {
                if (player.isSneaking && itemInteractable != null && itemInteractable.placeable != Placement.NONE) {
                    itemInteractable.place(this)
                } else if (!player.isSneaking) {
                    setUseInteractedBlock(Event.Result.DENY)
                    setUseItemInHand(Event.Result.DENY)
                    entityInteractable.interact(entity, player)
                }
                return
            }

            if (itemInteractable != null) {
                if (itemInteractable.placeable == Placement.NONE) {
                    setUseInteractedBlock(Event.Result.DENY)
                    setUseItemInHand(Event.Result.DENY)
                    itemInteractable.interact(player)
                } else {
                    itemInteractable.place(this)
                }
            }
        } else if (player.isSneaking && entityInteractable != null) {
            setUseInteractedBlock(Event.Result.DENY)
            entityInteractable.remove(entity, player)
        }
    }

    @EventHandler
    fun PlayerInteractAtEntityEvent.interaction() {
        if (isCancelled) return
        if (hand != EquipmentSlot.HAND) return // dumb
        val interactable = Interactables.find(rightClicked) ?: return

        interactable.interact(rightClicked, player)
    }

    @EventHandler
    fun BlockPlaceEvent.blockPlace() {
        isCancelled = blockPlaced.hasInteractable()
    }

    @EventHandler
    fun BlockBreakEvent.blockBreak() {
        isCancelled = block.hasInteractable()
    }

    @EventHandler
    fun EntityDamageByEntityEvent.remove() {
        if (isCancelled) return
        if (entity.passengers.isNotEmpty()) return

        val interactable = Interactables.find(entity) ?: return
        if (!interactable.removable) return

        interactable.remove(entity, damager)
    }

    @EventHandler
    fun BlockPistonExtendEvent.pistonBreak() {
        if (isCancelled) return

        (blocks.map { it.getRelative(direction) } + block.getRelative(direction))
            .forEach { destination -> // sick of ts
                destination.world
                    .getNearbyEntities(BoundingBox.of(destination))
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