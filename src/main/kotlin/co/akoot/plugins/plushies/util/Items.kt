package co.akoot.plugins.plushies.util

import co.akoot.plugins.bluefox.BlueFox
import co.akoot.plugins.bluefox.api.FoxConfig
import co.akoot.plugins.bluefox.api.Kolor
import co.akoot.plugins.bluefox.api.economy.Coin
import co.akoot.plugins.bluefox.api.economy.Market
import co.akoot.plugins.bluefox.extensions.getPDC
import co.akoot.plugins.bluefox.extensions.hasPDC
import co.akoot.plugins.bluefox.extensions.removePDC
import co.akoot.plugins.bluefox.extensions.setPDC
import co.akoot.plugins.bluefox.util.ColorUtil.MONTH_COLOR
import co.akoot.plugins.bluefox.util.Text
import co.akoot.plugins.plushies.Plushies.Companion.customItemConfig
import co.akoot.plugins.plushies.Plushies.Companion.key
import co.akoot.plugins.plushies.Plushies.Companion.plushieConf
import co.akoot.plugins.plushies.util.ItemCreator.createItem
import co.akoot.plugins.plushies.util.builders.ItemBuilder
import io.papermc.paper.datacomponent.DataComponentTypes
import io.papermc.paper.registry.keys.tags.DamageTypeTagKeys
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import kotlin.collections.listOf
import kotlin.collections.mutableMapOf

object Items {

    data class PendingHead(val config: FoxConfig, val path: String, val key: NamespacedKey)
    val pendingHeads = mutableListOf<PendingHead>()

    val itemKey = key("item")
    val placeableKey = key("placeable")
    val hitSoundKey = key("hit.sound")
    val swingSoundKey = key("swing.sound")

    val ItemStack.isCustomItem: Boolean
        get() = itemMeta?.hasPDC(itemKey) == true

    val ItemStack.isPlaceable: Boolean
        get() = isPlushie || itemMeta?.hasPDC(placeableKey) == true

    val ItemStack.isPlushie: Boolean
        get() = type == Material.TOTEM_OF_UNDYING && itemMeta?.hasCustomModelDataComponent() == true

    var ItemStack.hitSound: String?
        get() = itemMeta?.getPDC<String>(key("hit.sound"))
        set(value) {
            if (value != null) { ItemBuilder.builder(this).hitSound(value).build()
            }
        }

    var ItemStack.swingSound: String?
        get() = itemMeta?.getPDC(swingSoundKey)
        set(value) {
            val meta = itemMeta ?: return
            if (value == null || value in setOf("none", "-c", "null"))
                meta.removePDC(swingSoundKey)
            else
                meta.setPDC(swingSoundKey, value)
            itemMeta = meta
        }

    var plushies = plushieConf.getKeys().map { name -> name to (plushieConf.getString(name).takeUnless { it == "0" } ?: name) }
    val customItems = mutableMapOf<String, ItemStack>()

    fun loadItems() {
        // Load ItemConfig
        for (key in customItemConfig.getKeys()) {
            customItems[key.lowercase()] = createItem(customItemConfig, key, itemKey) ?: continue
        }

        customItems["wrench"] = ItemBuilder.builder(Material.POISONOUS_POTATO) // lol
            .itemName(Text("Wrench").component)
            .unsetData(DataComponentTypes.CONSUMABLE)
            .pdc(itemKey, "wrench")
            .itemModel("trial_key")
            .build()
    }

//    fun updateItem(item: ItemStack?): ItemStack? {
//        if (item == null) return null
//        val id = item.itemMeta?.getPDC<String>(itemKey) ?: return null
//        val customItem = customItems[id] ?: return null
//
//        if (item.type == customItem.type) return null
//        return item.withType(customItem.type)
//    }
//
//    fun updateInventory(inv: Inventory) {
//        for (i in 0 until inv.size) {
//            val new = updateItem(inv.getItem(i))
//            if (new != null) inv.setItem(i, new)
//        }
//    }

    fun createPlushie(name: String, customModelData: String): ItemStack {
        return ItemBuilder.builder(ItemStack(Material.TOTEM_OF_UNDYING))
            .itemName((Text(name).color(MONTH_COLOR)).component)
            .customModelData(customModelData)
            .damageResistance(DamageTypeTagKeys.IS_FIRE)
            .deathProtection(false) // cannot believe i was using a listener for this
            .build()
    }

    val hopcoin = ItemBuilder.builder(Material.POISONOUS_POTATO)
        .unsetData(DataComponentTypes.CONSUMABLE)
        .itemName(Text("HopCoin", Kolor.NUMBER).component)
        .pdc(BlueFox.key("ticker"), "hopcoin")
        .customModelData("hopcoin")
        .glint()
        .build()

    fun hopcoin() {
        Market.getCoin("hopcoin")?.let {
            Market.coins["hopcoin"] = Coin(
                id = it.id,
                ticker = it.ticker,
                name = "HopCoin™",
                description = "the world’s most valuable coin",
                backing = hopcoin
            )
        }
    }
}