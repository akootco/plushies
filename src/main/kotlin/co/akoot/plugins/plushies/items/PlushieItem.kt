package co.akoot.plugins.plushies.items

import co.akoot.plugins.bluefox.api.FoxConfig
import co.akoot.plugins.bluefox.util.ColorUtil.MONTH_COLOR
import co.akoot.plugins.bluefox.util.Text
import co.akoot.plugins.plushies.Plushies.Companion.plushieConf
import co.akoot.plugins.plushies.util.builders.ItemBuilder
import io.papermc.paper.registry.keys.tags.DamageTypeTagKeys
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

object PlushieItems {

    var plushies = load(plushieConf)

    fun load(config: FoxConfig): List<PlushieItem> {
        return config.getKeys().map { id ->
            PlushieItem(
                id = id,
                name = id.replace("_.*".toRegex(), ""),
                model = config.getString(id).takeUnless { it == "0" } ?: id
            )
        }
    }
}

data class PlushieItem(
    val id: String,
    val name: String,
    val model: String
) {
    fun item(model: String = this@PlushieItem.model): ItemStack {
        return ItemBuilder.builder(ItemStack(Material.TOTEM_OF_UNDYING))
            .itemName(Text(name).color(MONTH_COLOR).component)
            .customModelData(model)
            .damageResistance(DamageTypeTagKeys.IS_FIRE)
            .deathProtection(false)
            .build()
    }
}