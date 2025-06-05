package co.akoot.plugins.plushies.util

import co.akoot.plugins.bluefox.api.FoxConfig
import co.akoot.plugins.bluefox.extensions.getPDC
import co.akoot.plugins.bluefox.util.ColorUtil.MONTH_COLOR
import co.akoot.plugins.bluefox.util.Text
import co.akoot.plugins.plushies.Plushies.Companion.key
import co.akoot.plugins.plushies.Plushies.Companion.plushieConf
import co.akoot.plugins.plushies.listeners.handlers.boxingGlove
import co.akoot.plugins.plushies.util.ItemCreator.createItem
import co.akoot.plugins.plushies.util.builders.ItemBuilder
import io.papermc.paper.registry.keys.tags.DamageTypeTagKeys
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

object Items {

    val itemKey = key("item")
    val placeableKey = key("placeable")

    val ItemStack.isCustomItem: Boolean
        get() = itemMeta?.getPDC<String>(itemKey) != null

    val ItemStack.isPlaceable: Boolean
        get() = isPlushie || itemMeta?.getPDC<Boolean>(placeableKey) != null

    val ItemStack.isPlushie: Boolean
        get() = type == Material.TOTEM_OF_UNDYING && itemMeta?.hasCustomModelData() == true


    var plushies = plushieConf.getKeys().mapNotNull { name -> plushieConf.getInt(name)?.let { name to it } }
    val customItems: MutableMap<String, ItemStack> = HashMap()

    fun loadItems(config: FoxConfig) {
        customItems["boxing_glove"] = boxingGlove
        for (key in config.getKeys()) {
            customItems[key.lowercase()] = createItem(config, key, itemKey) ?: continue
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