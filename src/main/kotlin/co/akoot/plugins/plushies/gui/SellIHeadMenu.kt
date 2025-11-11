package co.akoot.plugins.plushies.gui

import co.akoot.plugins.bluefox.api.Kolor
import co.akoot.plugins.bluefox.api.economy.Coin
import co.akoot.plugins.bluefox.api.economy.Wallet.Companion.WORLD
import co.akoot.plugins.bluefox.extensions.isSurventure
import co.akoot.plugins.bluefox.extensions.wallet
import co.akoot.plugins.bluefox.util.ColorUtil.randomColor
import co.akoot.plugins.bluefox.util.Text
import co.akoot.plugins.bluefox.util.runLater
import co.akoot.plugins.plushies.util.builders.ChestGUI
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import java.math.BigDecimal

class SellIHeadMenu() : InventoryHolder {

    companion object {

        fun onClick(player: Player, event: InventoryClickEvent) {
            val cursor = event.cursor.type == Material.PLAYER_HEAD
            val current = event.currentItem != null && (event.currentItem?.type == Material.PLAYER_HEAD)
            event.isCancelled = !player.isSurventure || (!cursor && !current)
        }

        fun onClose(player: Player, event: InventoryCloseEvent) {
            runLater(1) {
                var heads = 0

                event.inventory.contents.filterNotNull().forEach { item ->
                    if (item.type == Material.PLAYER_HEAD) heads += item.amount
                    else player.give(item)
                }

                player.wallet?.let { wallet ->
                    if (heads > 0) {
                        val amount = BigDecimal(heads * 3)
                        WORLD.send(wallet, Coin.DIA, amount)
                        Text(player) { Kolor.ACCENT("Sold ") + heads + " heads for " + amount + $$" $DIA" }
                    }
                }
            }
        }
    }

    override fun getInventory(): Inventory {
        return ChestGUI.builder(54, this, false)
            .title(Text("Item Trade-In").color(randomColor(brightness = 0.6f)).component)
            .build()
    }
}