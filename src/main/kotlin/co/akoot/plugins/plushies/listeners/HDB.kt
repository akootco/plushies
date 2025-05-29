package co.akoot.plugins.plushies.listeners

import co.akoot.plugins.bluefox.api.Kolor
import co.akoot.plugins.bluefox.api.economy.Coin
import co.akoot.plugins.bluefox.api.economy.Economy.Error.INSUFFICIENT_BALANCE
import co.akoot.plugins.bluefox.api.economy.Economy.Error.MISSING_COIN
import co.akoot.plugins.bluefox.api.economy.Wallet
import co.akoot.plugins.bluefox.extensions.isSurventure
import co.akoot.plugins.bluefox.extensions.wallet
import co.akoot.plugins.bluefox.util.Text
import me.arcaniax.hdb.api.PlayerClickHeadEvent
import me.arcaniax.hdb.enums.CategoryEnum
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import java.math.BigDecimal

class HDB : Listener {
    // TODO: allow players to sell head back
    private val CategoryEnum.price: BigDecimal
        get() = when (this) {
            CategoryEnum.ONLINE_PLAYERS -> BigDecimal("9.0")
            else -> BigDecimal("3.0")
        }

    @EventHandler
    fun PlayerClickHeadEvent.buyHead() {
        if (!player.isSurventure) return
        val price = categoryEnum.price

        when (player.wallet?.send(Wallet.WORLD, Coin.DIA, price)) {
            MISSING_COIN, INSUFFICIENT_BALANCE -> {
                Text(player) { Kolor.ERROR("You are broke! This head costs $price \$DIA") }
                isCancelled = true
            }
        }
    }
}