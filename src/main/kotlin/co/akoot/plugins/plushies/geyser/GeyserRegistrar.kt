package co.akoot.plugins.plushies.geyser

import co.akoot.plugins.plushies.geyser.GeyserUtil.register
import co.akoot.plugins.plushies.items.PlushieItems.plushies
import co.akoot.plugins.plushies.util.Items.customItems
import co.akoot.plugins.plushies.util.builders.ItemBuilder
import org.bukkit.Material
import org.bukkit.inventory.meta.SkullMeta
import org.geysermc.geyser.api.GeyserApi
import org.geysermc.geyser.api.event.EventRegistrar
import org.geysermc.geyser.api.event.lifecycle.GeyserDefineCustomItemsEvent
import org.geysermc.geyser.api.event.lifecycle.GeyserDefineCustomSkullsEvent

class GeyserRegistrar : EventRegistrar {

    init {
        GeyserApi.api().eventBus().subscribe(this, GeyserDefineCustomItemsEvent::class.java, this::registerItems)
        GeyserApi.api().eventBus().subscribe(this, GeyserDefineCustomSkullsEvent::class.java, this::registerHeads)
    }

    fun registerItems(event: GeyserDefineCustomItemsEvent) {
        // register plushies

        for (item in plushies) register(event, item.item(), item.id.lowercase())

        // book covers
        for (int in 1..72) register(
            event, ItemBuilder.builder(Material.WRITTEN_BOOK)
                .customModelData(int).build(), "book.$int"
        )

        // everything else
        for ((_, items) in customItems.entries.groupBy { it.value.type }) {
            for ((key, itemStack) in items) {
                register(event, itemStack, key)
            }
        }
    }

    fun registerHeads(event: GeyserDefineCustomSkullsEvent) {
        for (item in customItems.values.filter { it.type == Material.PLAYER_HEAD }) {
            val texture = (item.itemMeta as SkullMeta).playerProfile?.textures?.skin?.path ?: return
            event.register(texture.substringAfterLast("/"),
                GeyserDefineCustomSkullsEvent.SkullTextureType.SKIN_HASH)
        }
    }
}