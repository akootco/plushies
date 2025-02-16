package co.akoot.plugins.plushies.util

import co.akoot.plugins.bluefox.util.ColorUtil.MONTH_COLOR
import co.akoot.plugins.bluefox.util.Text
import co.akoot.plugins.plushies.Plushies.Companion.plushieConf
import co.akoot.plugins.plushies.util.builders.ItemBuilder
import io.papermc.paper.registry.keys.tags.DamageTypeTagKeys
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

object Plush {

    var plushies = plushieConf.getKeys().mapNotNull { name -> plushieConf.getInt(name)?.let { name to it } }

    fun plushMsg(name: String): Text {
        return Text()
            .plus(Text("Please cherish this ").color("text"))
            .plus(Text(name).color("accent"))
            .plus(Text(" plushie forever").color("text"))
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