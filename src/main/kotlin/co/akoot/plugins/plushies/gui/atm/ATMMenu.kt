package co.akoot.plugins.plushies.gui.atm

import co.akoot.plugins.bluefox.api.Kolor
import co.akoot.plugins.bluefox.api.economy.Economy.rounded
import co.akoot.plugins.bluefox.api.economy.Market
import co.akoot.plugins.bluefox.extensions.isSurventure
import co.akoot.plugins.bluefox.extensions.wallet
import co.akoot.plugins.bluefox.util.ColorUtil.randomColor
import co.akoot.plugins.bluefox.util.Text
import co.akoot.plugins.bluefox.util.Text.Companion.asString
import co.akoot.plugins.plushies.util.Util.inValidWorld
import co.akoot.plugins.plushies.util.builders.ChestGUI
import co.akoot.plugins.plushies.util.builders.ItemBuilder
import io.papermc.paper.datacomponent.DataComponentTypes
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack
import java.math.BigDecimal

class ATMMenu(private val p: Player) : InventoryHolder {

    companion object {
        fun atmMainMenu(item: ItemStack, p: Player) {
            val name = item.itemMeta.itemName().asString()
            val coin = Market.getCoin(name.substring(1))?: return
            if (p.inValidWorld() && p.isSurventure) p.openInventory(CoinMenu(p, coin).inventory)
        }
    }

    override fun getInventory(): Inventory {
        // gui will grow as more coins with backing items are added, not gonna bother supporting more than 54 coins.
        val coins = Market.coins.filter { it.value.backing != null }
        return ChestGUI.builder(coins.size, this, true)
            .title(Text("Coins").color(randomColor(brightness = 0.6f)).component)
            .apply {
                // set buttons, will take player to balance when clicked
                coins.values.forEachIndexed { slot, coin ->
                    val backingItem = coin.backing?.clone()!!
                    val item = ItemBuilder.builder(backingItem)
                        .itemName(Kolor.ACCENT("$${coin.ticker}").component)
                        .unsetData(DataComponentTypes.CUSTOM_NAME)
                        .lore(listOf((p.wallet?.balance?.get(coin)?: BigDecimal.ZERO).rounded.component))
                        .build()
                    setItem(slot, item)
                }
            }.build()
    }
}