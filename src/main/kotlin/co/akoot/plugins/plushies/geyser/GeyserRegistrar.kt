package co.akoot.plugins.plushies.geyser

import co.akoot.plugins.plushies.geyser.GeyserUtil.register
import co.akoot.plugins.plushies.util.Items
import co.akoot.plugins.plushies.util.Items.customItems
import org.bukkit.Material
import org.bukkit.inventory.meta.SkullMeta
import org.geysermc.geyser.api.event.EventRegistrar
import org.geysermc.geyser.api.event.lifecycle.GeyserDefineCustomItemsEvent
import org.geysermc.geyser.api.event.lifecycle.GeyserDefineCustomSkullsEvent

class GeyserRegistrar : EventRegistrar {

    fun registerItems(event: GeyserDefineCustomItemsEvent) {
        // register plushies
        for (key in Items.plushies.sortedBy { it.second }) {
            register(event, key.first, key.second, "totem_of_undying")
            register(event, "${key.first}.st", key.second + 1, "totem_of_undying")
        }

        // book covers
        (1..72).forEach { cmd ->
            register(event, "book.$cmd", cmd, "written_book")
        }

        /*
         * register all other items here
         *
         * The double sort needs to happen
         * or the item textures will be on the wrong items.
         * Don't hate on geyser too much, it's the same for java!
         */

        // sort and group by material
        for ((material, keys) in customItems.entries.groupBy { it.value.type }) {
            // now sort by custom model data
            val sortedItems = keys.filter { it.value.itemMeta.hasCustomModelData() }
                .sortedBy { it.value.itemMeta.customModelData }

            // go on, register it then
            for (key in sortedItems) {
                val cmd = key.value.itemMeta.customModelData
                register(event, key.key, cmd, material.name.lowercase())
            }
        }
    }

    // this will create a texture pack that allows geyser to show heads in the inventory
    fun registerHeads(event: GeyserDefineCustomSkullsEvent) {
        for (item in customItems.values.filter { it.type == Material.PLAYER_HEAD }) {
            // get the skin texture path
            val texture = (item.itemMeta as SkullMeta).playerProfile?.textures?.skin?.path ?: return
            // split path to only get the ID and register it
            event.register(texture.substringAfterLast("/"),
                GeyserDefineCustomSkullsEvent.SkullTextureType.SKIN_HASH)
        }
    }
}