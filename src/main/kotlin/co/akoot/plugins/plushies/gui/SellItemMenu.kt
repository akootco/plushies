package co.akoot.plugins.plushies.gui

import co.akoot.plugins.bluefox.api.Kolor
import co.akoot.plugins.bluefox.api.economy.Coin
import co.akoot.plugins.bluefox.api.economy.Market
import co.akoot.plugins.bluefox.api.economy.Wallet.Companion.WORLD
import co.akoot.plugins.bluefox.extensions.isSurventure
import co.akoot.plugins.bluefox.extensions.wallet
import co.akoot.plugins.bluefox.util.ColorUtil.randomColor
import co.akoot.plugins.bluefox.util.Text
import co.akoot.plugins.bluefox.util.runLater
import co.akoot.plugins.plushies.Plushies.Companion.conf
import co.akoot.plugins.plushies.util.builders.ChestGUI
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack
import java.math.BigDecimal

class SellItemMenu() : InventoryHolder {

    companion object {
        fun ItemStack.isTotem(): Boolean {
            return type == Material.TOTEM_OF_UNDYING && itemMeta?.hasCustomModelData() == false
        }

        fun onClick(player: Player, event: InventoryClickEvent) {
            val cursor = event.cursor.type == Material.PLAYER_HEAD || event.cursor.isTotem()
            val current = event.currentItem != null && (event.currentItem?.type == Material.PLAYER_HEAD || event.currentItem?.isTotem() == true)
            event.isCancelled = !player.isSurventure || (!cursor && !current)
        }

        fun onClose(player: Player, event: InventoryCloseEvent) {
            runLater(1) {
                var totems = 0
                var heads = 0

                event.inventory.contents.filterNotNull().forEach { item ->
                    when {
                        item.isTotem() -> totems += item.amount
                        item.type == Material.PLAYER_HEAD -> heads += item.amount
                        else -> player.give(item)
                    }
                }

                player.wallet?.let { wallet ->
                    if (totems > 0) {
                        val amount = BigDecimal(totems * 6)
                        WORLD.send(wallet, Market.getCoin("hopcoin")!!, amount)
                        Text(player) { Kolor.ACCENT("sold ") + totems + " totems for " + amount + $$" $hopcoin" }
                    }
                    if (heads > 0) {
                        val amount = BigDecimal(heads * 3)
                        WORLD.send(wallet, Coin.DIA, amount)
                        Text(player) { Kolor.ACCENT("sold ") + heads + " heads for " + amount + $$" $DIA" }
                    }
                }
                conf.increment("plushies_traded", totems)
            }
        }
    }

    override fun getInventory(): Inventory {
        return ChestGUI.builder(54, this, false)
            .title(Text("Item Trade-In").color(randomColor(brightness = 0.6f)).component)
            .build()
    }
}