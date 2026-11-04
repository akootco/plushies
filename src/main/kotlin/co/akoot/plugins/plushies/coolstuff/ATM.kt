package co.akoot.plugins.plushies.coolstuff

import co.akoot.plugins.bluefox.api.dialog
import co.akoot.plugins.bluefox.api.economy.Coin
import co.akoot.plugins.bluefox.api.economy.Economy.bd
import co.akoot.plugins.bluefox.api.economy.Market
import co.akoot.plugins.bluefox.extensions.countIncludingBlocks
import co.akoot.plugins.bluefox.extensions.countItem
import co.akoot.plugins.bluefox.extensions.wallet
import co.akoot.plugins.bluefox.util.error
import co.akoot.plugins.bluefox.util.text
import co.akoot.plugins.bluefox.util.zip
import co.akoot.plugins.plushies.Plushies.Companion.key
import co.akoot.plugins.plushies.api.Interactable
import co.akoot.plugins.plushies.api.Sound
import co.akoot.plugins.plushies.util.builders.ItemBuilder
import io.papermc.paper.datacomponent.DataComponentTypes
import io.papermc.paper.dialog.Dialog
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerInteractEvent
import org.joml.Vector3f

object ATM : Interactable {
    override val key = key("atm")
    override fun height() = 2f
    override fun width() = 0.9f
    override fun translation() = Vector3f(0f, 0.5f, 0f)
    override val cancelPlacement = true
    override val removable = true
    override val rotatable = true
    override val setOwner = true

    override fun item() = ItemBuilder.builder(Material.POISONOUS_POTATO)
        .itemName("ATM".text)
        .stackSize(16)
        .itemModel(key.toString())
        .unsetData(DataComponentTypes.CONSUMABLE)
        .unsetData(DataComponentTypes.FOOD)
        .pdc(key)
        .build()

    override val breakSound = Sound("block.iron.break")
    override val placeSound = Sound("block.iron.place")

    override fun interact(entity: Entity, player: Player) {
        player.showDialog(mainMenu())
        super.interact(player)
    }

    override fun place(event: PlayerInteractEvent): Boolean {
        if (event.blockFace != BlockFace.UP) return false
        return super.place(event)
    }

    private fun coinScreen(coin: Coin, player: Player): Dialog {
        fun currentBalance() = player.wallet?.balance?.get(coin) ?: 0.bd

        return dialog {
            closeWithEscape(true)
            title(coin.ticker.text)
            val balance = currentBalance()
            message("Balance: ${balance.toInt()}")

            if (balance >= 1.bd) {
                slider(300, "amount", "Amount".text, 1f..balance.toFloat().coerceAtMost(500f))
                button(150, "Withdraw".text) { _, view ->
                    val amount = view.getFloat("amount")?.toInt() ?: 0
                    player.wallet?.withdraw(player, coin,amount)
                    player.msg("withdrew", coin, amount)
                }
            } else {
                message(300,"not enough funds to withdraw!".error)
            }

            button(150, "Deposit".text) { _, _ ->
                val backing = coin.backing
                val backingBlock = coin.backingBlock

                val amount = when {
                    backing == null -> 0
                    backingBlock != null -> player.countIncludingBlocks(backing, backingBlock, coin.backingBlockValue)
                    else -> player.countItem(backing)
                }

                if (amount > 0) {
                    player.wallet?.deposit(player, coin, amount)
                    player.msg("deposited", coin, amount)
                }
            }
        }
    }

    private fun mainMenu(): Dialog {
        return dialog {
            columns(1)
            closeWithEscape(true)
            title("ATM".text)

            for (coin in Market.coins.filter { it.value.backing != null }) {
                button(150, "$${coin.key}".text) { p, _ ->
                    p.showDialog(coinScreen(coin.value, p))
                }
            }
        }
    }

    private fun Player.msg(action: String, coin: Coin, amount: Int) {
        val balance = wallet?.balance?.get(coin)?.toInt() ?: 0
        sendActionBar(text("You $action ", amount, " $${coin.ticker}. ", "Balance: ", balance).zip)
    }
}