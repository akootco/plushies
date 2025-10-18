package co.akoot.plugins.plushies.util

import co.akoot.plugins.bluefox.api.FoxConfig
import co.akoot.plugins.bluefox.extensions.getPDC
import co.akoot.plugins.bluefox.extensions.hasPDC
import co.akoot.plugins.bluefox.util.ColorUtil.MONTH_COLOR
import co.akoot.plugins.bluefox.util.Text
import co.akoot.plugins.plushies.Plushies.Companion.customBlockConfig
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
import kotlin.collections.mutableMapOf

object Items {

    data class PendingHead(val config: FoxConfig, val path: String, val key: NamespacedKey)
    val pendingHeads = mutableListOf<PendingHead>()

    val itemKey = key("item")
    val placeableKey = key("placeable")
    val hitSoundKey = key("hit.sound")

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

    var plushies = plushieConf.getKeys().map { name -> name to (plushieConf.getString(name).takeUnless { it == "0" } ?: name) }
    val customItems = mutableMapOf<String, ItemStack>()

    fun loadItems() {
        // Load ItemConfig
        for (key in customItemConfig.getKeys()) {
            customItems[key.lowercase()] = createItem(customItemConfig, key, itemKey) ?: continue
        }
        // Load BlockConfig
        for (key in customBlockConfig.getKeys()) {
            customItems[key.lowercase()] = createItem(customBlockConfig, key, itemKey) ?: continue
        }

        customItems["wrench"] = ItemBuilder.builder(Material.POISONOUS_POTATO) // lol
            .itemName(Text("Wrench").component)
            .unsetData(DataComponentTypes.CONSUMABLE)
            .pdc(itemKey, "wrench")
            .itemModel("trial_key")
            .build()
    }

    fun updateItem(item: ItemStack?): ItemStack? {
        if (item == null) return null
        val id = item.itemMeta?.getPDC<String>(itemKey) ?: return null
        val customItem = customItems[id] ?: return null

        if (item.isSimilar(customItem)) return null
        return customItem.clone().apply { amount = item.amount }
    }

    fun updateInventory(inv: Inventory) {
        for (i in 0 until inv.size) {
            val new = updateItem(inv.getItem(i))
            if (new != null) inv.setItem(i, new)
        }
    }

    fun createPlushie(name: String, customModelData: String): ItemStack {
        return ItemBuilder.builder(ItemStack(Material.TOTEM_OF_UNDYING))
            .itemName((Text(name).color(MONTH_COLOR)).component)
            .customModelData(customModelData)
            .damageResistance(DamageTypeTagKeys.IS_FIRE)
            .deathProtection(false) // cannot believe i was using a listener for this
            .build()
    }
}