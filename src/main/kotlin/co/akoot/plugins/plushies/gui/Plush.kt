package co.akoot.plugins.plushies.gui

import co.akoot.plugins.bluefox.util.ColorUtil.MONTH_COLOR
import co.akoot.plugins.bluefox.util.Txt
import co.akoot.plugins.plushies.Plushies.Configs.pConf
import co.akoot.plugins.plushies.util.builders.ItemBuilder
import io.papermc.paper.registry.keys.tags.DamageTypeTagKeys
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import kotlin.math.min

    object Plush {

        /*
        Assign an odd number to each plushie to use for custom model data,
        statues use even numbers, we can ignore those
         */
        val plushies = pConf.getStringList("plushies").mapIndexed { index, string -> string to (index * 2) + 1 }

        // plushie menu nav button
        val pMenu = ItemBuilder.builder(ItemStack(Material.TOTEM_OF_UNDYING))
            .itemName(Txt("Plushies").color(NamedTextColor.GREEN).c)
            .build()

        // statue menu nav button
        val sMenu = ItemBuilder.builder(ItemStack(Material.ARMOR_STAND))
            .itemName(Txt("Statues").color(NamedTextColor.GREEN).c)
            .build()

        // main menu nav button
        val home = ItemBuilder.builder(ItemStack(Material.PURPLE_BED))
            .itemName(Txt("Home").color(NamedTextColor.GREEN).c)
            .build()

        val nextPage = ItemBuilder.builder(ItemStack(Material.PAPER))
            .itemName(Txt("→").color(NamedTextColor.GOLD).c).build()

        val prevPage = ItemBuilder.builder(ItemStack(Material.PAPER))
            .itemName(Txt("←").color(NamedTextColor.GOLD).c).build()

        // create list of plushie items
        fun setPlushies(pageNumber: Int): List<ItemStack> {
            val plushList = mutableListOf<ItemStack>()

            val start = (pageNumber - 1) * 45
            val end = min(start + 45, plushies.size)

            // only get what fits on the page
            for (index in start until end) {
                val plushie = plushies[index]
                val name = plushie.first.replace("_.*".toRegex(), "")
                plushList.add(createPlushie(name, plushie.second))
            }

            return plushList
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