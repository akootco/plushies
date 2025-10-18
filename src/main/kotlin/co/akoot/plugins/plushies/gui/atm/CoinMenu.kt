package co.akoot.plugins.plushies.gui.atm

import co.akoot.plugins.bluefox.api.economy.Coin
import co.akoot.plugins.bluefox.api.economy.Economy
import co.akoot.plugins.bluefox.api.economy.Wallet.Companion.WORLD
import co.akoot.plugins.bluefox.extensions.wallet
import co.akoot.plugins.bluefox.util.ColorUtil.randomColor
import co.akoot.plugins.bluefox.util.Text
import co.akoot.plugins.bluefox.util.runLater
import co.akoot.plugins.plushies.util.builders.ChestGUI
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack
import java.math.BigDecimal

class CoinMenu(private val p: Player, val coin: Coin) : InventoryHolder {

    companion object {
        fun onClick(holder: CoinMenu, event: InventoryClickEvent) {
            val allowedTypes = listOfNotNull(holder.coin.backing, holder.coin.backingBlock)

            if ((!event.cursor.isEmpty && event.cursor.type !in allowedTypes) ||
                (event.currentItem?.type !in allowedTypes)) {
                event.isCancelled = true
            }
        }

        fun onClose(holder: CoinMenu, player: Player, event: InventoryCloseEvent) {
            runLater(1) {
                var total = 0

                event.inventory.contents.filterNotNull().forEach { item ->
                    when (item.type) {
                        holder.coin.backing -> total += item.amount
                        holder.coin.backingBlock -> total += item.amount * 9
                        else -> player.dropItem(item)
                    }
                }

                val wallet = player.wallet ?: return@runLater
                val balance = wallet.balance[holder.coin]?.toInt() ?: 0

                if (total == balance) return@runLater

                if (total > balance) {
                    WORLD.send(wallet, holder.coin, BigDecimal(total - balance))
                } else {
                    wallet.send(WORLD, holder.coin, BigDecimal(balance - total))
                }

                Economy.sendBalance(player, wallet, holder.coin)
            }
        }
    }

    override fun getInventory(): Inventory {
        val inv = ChestGUI.builder(54, this, false)
            .title(Text("$${coin.ticker}").color(randomColor(brightness = 0.6f)).component)
            .build()

        val amount = (p.wallet?.balance?.get(coin) ?: BigDecimal.ZERO).toInt()
        if (amount > 0) {
            val itemMat = coin.backing ?: return inv
            val blockMat = coin.backingBlock

            if (blockMat != null) {
                val blocks = amount / 9
                val remainder = amount % 9

                if (blocks > 0) inv.addItem(ItemStack(blockMat, blocks))
                if (remainder > 0) inv.addItem(ItemStack(itemMat, remainder))
            } else {
                inv.addItem(ItemStack(itemMat, amount))
            }
        }

        return inv
    }
}