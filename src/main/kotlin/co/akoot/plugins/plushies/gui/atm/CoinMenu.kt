package co.akoot.plugins.plushies.gui.atm

import co.akoot.plugins.bluefox.api.economy.Coin
import co.akoot.plugins.bluefox.extensions.wallet
import co.akoot.plugins.bluefox.util.ColorUtil.randomColor
import co.akoot.plugins.bluefox.util.Text
import co.akoot.plugins.plushies.util.builders.ChestGUI
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack
import java.math.BigDecimal

class CoinMenu(private val p: Player, private val coin: Coin) : InventoryHolder {

    private fun showBal(): List<ItemStack> {
        val backingItems = mutableListOf<ItemStack>()
        // sorry but i had to steal this!
        val amount = (p.wallet?.balance?.get(coin)?: BigDecimal.ZERO).toInt()
        if (amount <= 0) return backingItems

        val blocks = amount / 9
        val remainder = amount % 9

        // need to separate stacks, so it's not all put into one slot
        if (blocks > 0) {
            var remainingBlocks = blocks
            while (remainingBlocks > 0) { // win?
                val stackSize = minOf(remainingBlocks, coin.backingBlock!!.maxStackSize)
                backingItems.add(ItemStack(coin.backingBlock!!, stackSize))
                remainingBlocks -= stackSize
            }
        }

        if (remainder > 0) {
            backingItems.add(ItemStack(coin.backing!!, remainder))
        }

        return backingItems
    }

    override fun getInventory(): Inventory {
        return ChestGUI.builder(54, this, false)
            .title(Text("$${coin.ticker}").color(randomColor(brightness = 0.6f)).component)
            .setItems(0..53, showBal())
            .build()
    }
}