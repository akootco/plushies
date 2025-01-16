package co.akoot.plugins.plushies.util

import co.akoot.plugins.bluefox.util.ColorUtil.MONTH_COLOR
import co.akoot.plugins.bluefox.util.Txt
import co.akoot.plugins.plushies.Plushies.Configs.pConf
import co.akoot.plugins.plushies.util.builders.ItemBuilder
import io.papermc.paper.registry.keys.tags.DamageTypeTagKeys
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

object Plush {

        val plushies = pConf.getStringList("plushies").map { string ->
            val parts = string.split(":")
            val name = parts[0]
            val cmd = parts[1].toInt()
            name to cmd
        }

        fun plushMsg(name: String): Txt {
            return Txt()
                .plus(Txt("Please cherish this ").color("text"))
                .plus(Txt(name).color("accent"))
                .plus(Txt(" plushie forever").color("text"))
        }

        fun createPlushie(name: String, customModelData: Int): ItemStack {
            return ItemBuilder.builder(ItemStack(Material.TOTEM_OF_UNDYING))
                .itemName((Txt(name).color(MONTH_COLOR)).c)
                .customModelData(customModelData)
                .damageResistance(DamageTypeTagKeys.IS_FIRE)
                .deathProtection(false) // cannot believe i was using a listener for this
                .build()
        }
}