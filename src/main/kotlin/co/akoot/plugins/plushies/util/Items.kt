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
import co.akoot.plugins.bluefox.util.Color.Green
import co.akoot.plugins.bluefox.util.ColorUtil.MONTH_COLOR
import co.akoot.plugins.bluefox.util.Text
import co.akoot.plugins.bluefox.util.text
import co.akoot.plugins.plushies.Plushies.Companion.customItemConfig
import co.akoot.plugins.plushies.Plushies.Companion.key
import co.akoot.plugins.plushies.Plushies.Companion.plushieConf
import co.akoot.plugins.plushies.coolstuff.cushionKey
import co.akoot.plugins.plushies.util.ItemCreator.createItem
import co.akoot.plugins.plushies.util.builders.ItemBuilder
import io.papermc.paper.datacomponent.DataComponentTypes
import io.papermc.paper.datacomponent.item.DyedItemColor
import io.papermc.paper.registry.keys.tags.DamageTypeTagKeys
import net.kyori.adventure.text.Component
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.Item
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

    val customItems = mutableMapOf<String, ItemStack>()

    fun registerItem(id: String, item: ItemStack) {
        customItems[id.lowercase()] = item
    }

    fun getItem(id: String): ItemStack? {
        return customItems[id.lowercase()]?.clone()
    }

    fun getItems(predicate: (ItemStack) -> Boolean): List<ItemStack> {
        return customItems.values.filter(predicate).map { it.clone() }
    }

    fun getRandomItem(predicate: (ItemStack) -> Boolean): ItemStack? {
        return getItems(predicate).randomOrNull()
    }

    fun getRandomItem(): ItemStack? {
        return getAllItems().randomOrNull()
    }

    fun getAllItems(): List<ItemStack> {
        return customItems.values.map { it.clone() }
    }

    fun loadItems() {
        // Load ItemConfig
        for (key in customItemConfig.getKeys()) {
            customItems[key.lowercase()] = createItem(customItemConfig, key, itemKey) ?: continue
        }

        registerItem("wrench", ItemBuilder.builder(Material.POISONOUS_POTATO) // lol
            .itemName(Text("Wrench").component)
            .unsetData(DataComponentTypes.CONSUMABLE)
            .pdc(itemKey, "wrench")
            .customModelData("wrench")
            .itemModel("trial_key")
            .build())

        registerItem("cushion", ItemBuilder.builder(Material.WHITE_CARPET)
            .itemName("Cushion".text)
            .pdc(itemKey, "cushion")
            .pdc(cushionKey)
            .stackSize(16)
            .customModelData("cushion")
            .build()
        )
    }

    val ItemStack.itemId: String?
        get() = itemMeta?.getPDC(itemKey)

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

    var ItemStack.xpBottle: Int?
        get() = itemMeta?.getPDC<Int>(key("stored_xp"))
        set(value) {
            if (value != null) {
                editMeta {
                    it.setPDC(key("stored_xp"), value)
                    it.lore(listOf(Component.text("Stored XP: $value points", Green)))
                }
            }
        }

    fun ItemStack.applyDye(color: Color): Boolean {
        val current = getData(DataComponentTypes.DYED_COLOR)?.color()
        val result = current?.mixColors(color) ?: color

        if (result == current) return false

        setData(
            DataComponentTypes.DYED_COLOR,
            DyedItemColor.dyedItemColor().color(result).build()
        )
        return true
    }

//    fun updateItem(item: ItemStack?): ItemStack? {
//        if (item == null) return null
//        val id = item.itemMeta?.getPDC<String>(itemKey) ?: return null
//        val customItem = getItem(id) ?: return null
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

    fun hopcoin() {
        val hopcoin = ItemBuilder.builder(Material.POISONOUS_POTATO)
            .unsetData(DataComponentTypes.CONSUMABLE)
            .itemName(Text("HopCoin", Kolor.NUMBER).component)
            .pdc(BlueFox.key("ticker"), "hopcoin")
            .customModelData("hopcoin")
            .glint()
            .build()

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