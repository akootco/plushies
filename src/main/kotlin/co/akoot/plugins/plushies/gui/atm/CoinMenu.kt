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

        val itemMat = coin.backing ?: return backingItems
        val blockMat = coin.backingBlock

        // need to separate stacks, so it's not all put into one slot
        if (blockMat != null) {
            // if theres a backing block, use it here
            val blocks = amount / 9
            val remainder = amount % 9

            if (blocks > 0) {
                var remainingBlocks = blocks
                while (remainingBlocks > 0) {
                    val stackSize = minOf(remainingBlocks, blockMat.maxStackSize)
                    backingItems.add(ItemStack(blockMat, stackSize))
                    remainingBlocks -= stackSize
                }
            }

            if (remainder > 0) {
                backingItems.add(ItemStack(itemMat, remainder))
            }
        } else {
            // no backing block use items. fixes $AD menu issue
            var remainingItems = amount
            while (remainingItems > 0) {
                val stackSize = minOf(remainingItems, itemMat.maxStackSize)
                backingItems.add(ItemStack(itemMat, stackSize))
                remainingItems -= stackSize
            }
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