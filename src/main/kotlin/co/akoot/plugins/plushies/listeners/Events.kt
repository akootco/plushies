package co.akoot.plugins.plushies.listeners

import co.akoot.plugins.bluefox.extensions.getPDC
import co.akoot.plugins.bluefox.extensions.hasMeta
import co.akoot.plugins.bluefox.extensions.hasPDC
import co.akoot.plugins.bluefox.util.toBukkitColor
import co.akoot.plugins.plushies.Plushies.Companion.key
import co.akoot.plugins.plushies.listeners.handlers.dropItem
import co.akoot.plugins.plushies.listeners.handlers.plushieFrame
import co.akoot.plugins.plushies.listeners.tasks.Golf.Companion.golfKey
import co.akoot.plugins.plushies.listeners.tasks.Golf.Companion.spawnGolfBall
import co.akoot.plugins.plushies.util.Items.updateItem
import co.akoot.plugins.plushies.util.builders.ItemBuilder
import co.akoot.plugins.plushies.util.isCustomBlock
import com.destroystokyo.paper.MaterialTags
import com.destroystokyo.paper.event.block.BeaconEffectEvent
import io.papermc.paper.datacomponent.DataComponentTypes
import io.papermc.paper.event.player.PlayerItemFrameChangeEvent
import io.papermc.paper.event.player.PlayerItemFrameChangeEvent.ItemFrameChangeAction
import org.bukkit.DyeColor
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.ItemDisplay
import org.bukkit.entity.ItemFrame
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.hanging.HangingBreakEvent
import org.bukkit.event.hanging.HangingPlaceEvent
import org.bukkit.event.inventory.PrepareItemCraftEvent
import org.bukkit.event.player.PlayerArmorStandManipulateEvent
import org.bukkit.event.world.ChunkLoadEvent
import org.bukkit.potion.PotionEffectType

class Events : Listener {

    @EventHandler
    fun updateDisplays(event: ChunkLoadEvent) {
        for (entity in event.chunk.entities) {
            when (entity) {
                is ItemFrame -> updateItem(entity.item)?.let { entity.setItem(it) }
//                is ItemDisplay -> {
//                    if (!entity.location.block.isCustomBlock)
//                        updateItem(entity.itemStack)?.let { entity.setItemStack(it) }
//                }
            }
        }
    }

    @EventHandler
    fun PrepareItemCraftEvent.elytra() {
        if (inventory.matrix.filterNotNull().size != 2) return
        val wings = inventory.matrix.firstOrNull { it?.type == Material.ELYTRA } ?: return

        val dye = inventory.matrix.firstOrNull { item ->
            item != null && MaterialTags.DYES.isTagged(item.type)
        } ?: return

        val dyeColor = DyeColor.valueOf(dye.type.name.removeSuffix("_DYE")).color
        val wingColor = wings.getData(DataComponentTypes.DYED_COLOR)?.color()

        val finalColor = when {
            dye.itemMeta?.hasPDC(NamespacedKey("choco", "rgb_dye")) == true ->
                dye.itemMeta?.displayName()?.color()?.toBukkitColor()

            wingColor != null -> wingColor.mixColors(dyeColor)

            else -> dyeColor
        }

        inventory.result = ItemBuilder.builder(wings.clone()).dye(finalColor?: return).build()
    }

    @EventHandler
    fun armorStandEdit(event: PlayerArmorStandManipulateEvent) {
        val clicked = event.rightClicked
        if (clicked.getPDC<String>(golfKey) != null) {
            clicked.remove()
        }
    }

    @EventHandler
    fun blockPlace(event: BlockPlaceEvent) {
        event.isCancelled = (event.player.hasMeta("placedPlushie")) ||
                spawnGolfBall(event.player, event.block.location.add(0.5, 0.0, 0.5))
    }

    @EventHandler
    fun blockPlace(event: HangingPlaceEvent) {
        val player = event.player ?: return
        event.isCancelled = (player.hasMeta("placedPlushie"))
    }

    @EventHandler
    fun PlayerItemFrameChangeEvent.frameChange() {
        if (itemFrame.getPDC<Boolean>(plushieFrame) != null &&
            action == ItemFrameChangeAction.REMOVE) { itemFrame.remove() }
    }

    @EventHandler
    fun itemFrameBreak(event: HangingBreakEvent) {
        dropItem(event.entity as? ItemFrame ?: return)
    }

    @EventHandler
    fun BeaconEffectEvent.beaconEffect() {
        isCancelled = effect.type == PotionEffectType.JUMP_BOOST && player.hasPDC(key("plsnojump"))
        effect = effect.withDuration(effect.duration * 2)
    }
}