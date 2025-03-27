package co.akoot.plugins.plushies.util

import co.akoot.plugins.bluefox.api.FoxConfig
import co.akoot.plugins.bluefox.util.ColorUtil.MONTH_COLOR
import co.akoot.plugins.bluefox.util.Text
import co.akoot.plugins.plushies.Plushies.Companion.key
import co.akoot.plugins.plushies.Plushies.Companion.plushieConf
import co.akoot.plugins.plushies.util.ItemCreator.createItem
import co.akoot.plugins.plushies.util.builders.EquippableBuilder
import co.akoot.plugins.plushies.util.builders.ItemBuilder
import io.papermc.paper.registry.keys.tags.DamageTypeTagKeys
import net.kyori.adventure.text.format.NamedTextColor.GOLD
import org.bukkit.Material
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack

object Items {

    var plushies = plushieConf.getKeys().mapNotNull { name -> plushieConf.getInt(name)?.let { name to it } }
    val customItems: MutableMap<String, ItemStack> = HashMap()

    fun loadItems(config: FoxConfig) {
        for (key in config.getKeys()) {
            customItems[key.lowercase()] = createItem(config, key, key("item")) ?: continue
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

    fun partyHat(name: String = ""): ItemStack {
        val item = ItemBuilder.builder(ItemStack(Material.POPPED_CHORUS_FRUIT))
            .itemName((Text("Party Hat").color(MONTH_COLOR)).component)
            .customModelData(1)
            .apply {
                if (name.isNotBlank()) {
                    lore(listOf(Text("Happy Birthday $name", GOLD).component))
                }
            }.build()

        return EquippableBuilder.builder(item, EquipmentSlot.HEAD).build()
    }
}