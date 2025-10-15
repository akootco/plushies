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