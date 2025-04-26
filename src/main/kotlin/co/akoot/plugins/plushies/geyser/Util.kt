package co.akoot.plugins.plushies.geyser

import org.bukkit.Tag
import org.bukkit.Material
import org.geysermc.geyser.api.event.lifecycle.GeyserDefineCustomItemsEvent
import org.geysermc.geyser.api.item.custom.CustomItemData
import org.geysermc.geyser.api.item.custom.CustomItemOptions

object Helpers {

    fun register(event: GeyserDefineCustomItemsEvent, itemName: String, customModelData: Int, material: String) {
        val data = CustomItemData.builder()
            .customItemOptions(CustomItemOptions.builder().customModelData(customModelData).build())
            .name(itemName.lowercase())
            .allowOffhand(true)
            .displayHandheld(material.isHandheld())

        event.register("minecraft:${material.lowercase()}", data.build())
    }

    private fun String.isHandheld(): Boolean {
        return Tag.ITEMS_BREAKS_DECORATED_POTS.values
            .contains(Material.matchMaterial(this))
    }
}