package co.akoot.plugins.plushies.geyser

import co.akoot.plugins.plushies.geyser.GeyserUtil.register
import co.akoot.plugins.plushies.util.Items
import co.akoot.plugins.plushies.util.Items.customItems
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
        for ((name, value) in Items.plushies) {
            val asInt = value.toIntOrNull()
            register(event, name, asInt ?: value, "totem_of_undying")
            if (asInt != null) {
                register(event, "$name.st", asInt + 1, "totem_of_undying")
            }
        }

        // book covers
        (1..72).forEach { cmd ->
            register(event, "book.$cmd", cmd, "written_book")
        }

        // everything else
        for ((material, keys) in customItems.entries.groupBy { it.value.type }) {
            val sortedItems = keys.filter { it.value.itemMeta?.hasCustomModelDataComponent() == true }

            for (key in sortedItems) {
                val cmd = key.value.itemMeta.customModelDataComponent

                cmd.strings.firstOrNull()?.let {
                    register(event, key.key, it, material.name.lowercase())
                }

                cmd.floats.firstOrNull()?.let {
                    register(event, key.key, it, material.name.lowercase())
                }
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