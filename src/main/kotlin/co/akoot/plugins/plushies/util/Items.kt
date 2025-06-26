package co.akoot.plugins.plushies.util

import co.akoot.plugins.bluefox.extensions.hasPDC
import co.akoot.plugins.bluefox.util.ColorUtil.MONTH_COLOR
import co.akoot.plugins.bluefox.util.Text
import co.akoot.plugins.plushies.Plushies.Companion.customBlockConfig
import co.akoot.plugins.plushies.Plushies.Companion.customItemConfig
import co.akoot.plugins.plushies.Plushies.Companion.key
import co.akoot.plugins.plushies.Plushies.Companion.plushieConf
import co.akoot.plugins.plushies.util.ItemCreator.createItem
import co.akoot.plugins.plushies.util.builders.ItemBuilder
import io.papermc.paper.registry.keys.tags.DamageTypeTagKeys
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

object Items {

    val itemKey = key("item")
    val placeableKey = key("placeable")

    val ItemStack.isCustomItem: Boolean
        get() = itemMeta?.hasPDC(itemKey) == true

    val ItemStack.isPlaceable: Boolean
        get() = isPlushie || itemMeta?.hasPDC(placeableKey) == true

    val ItemStack.isPlushie: Boolean
        get() = type == Material.TOTEM_OF_UNDYING && itemMeta?.hasCustomModelData() == true


    var plushies = plushieConf.getKeys().mapNotNull { name -> plushieConf.getInt(name)?.let { name to it } }
    val customItems: MutableMap<String, ItemStack> = HashMap()

    fun loadItems() {
        // Load ItemConfig
        for (key in customItemConfig.getKeys()) {
            customItems[key.lowercase()] = createItem(customItemConfig, key, itemKey) ?: continue
        }
        // Load BlockConfig
        for (key in customBlockConfig.getKeys()) {
            customItems[key.lowercase()] = createItem(customBlockConfig, key, itemKey) ?: continue
        }
    }

    fun createPlushie(name: String, customModelData: Int): ItemStack {
        return ItemBuilder.builder(ItemStack(Material.TOTEM_OF_UNDYING))
            .itemName((Text(name).color(MONTH_COLOR)).component)
            .customModelData(customModelData)
            .damageResistance(DamageTypeTagKeys.IS_FIRE)
            .deathProtection(false) // cannot believe i was using a listener for this
            .build()
    }
}