package co.akoot.plugins.plushies.coolstuff.casino

import co.akoot.plugins.bluefox.api.economy.Coin
import co.akoot.plugins.bluefox.api.economy.Invoice
import co.akoot.plugins.bluefox.api.economy.Wallet
import co.akoot.plugins.bluefox.extensions.buy
import co.akoot.plugins.bluefox.extensions.wallet
import co.akoot.plugins.bluefox.util.text
import co.akoot.plugins.bluefox.util.zip
import co.akoot.plugins.plushies.Plushies
import co.akoot.plugins.plushies.api.Interactable
import co.akoot.plugins.plushies.api.Interactables
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import java.math.BigDecimal

object Casino {
    private val games = mutableMapOf<NamespacedKey, Interactable>()

    fun register(game: Interactable) {
        games[game.key] = game
        Interactables.register(game)
    }

    fun find(key: NamespacedKey): Interactable? =
        games[key]

    fun all(): Collection<Interactable> =
        games.values

    fun takePayment(
        player: Player,
        amount: Float,
        description: String,
        onSuccess: () -> Unit
    ) {
        val invoice = Invoice(
            amount.toBigDecimal(),
            Coin.hopcoin,
            description = description
        )

        player.buy(invoice) { success ->
            if (success) {
                onSuccess()
                Plushies.conf.increment("casino.buyin", amount.toDouble())
            }
        }
    }

    fun payout(player: Player, amount: Float) {
        val win = amount.toBigDecimal()
        if (win <= BigDecimal.ZERO) return
        
        player.wallet?.let {
            Wallet.BANK.send(it, Coin.hopcoin, win)
            player.sendActionBar(text("You won ", win, " ",Coin.hopcoin.ticker).zip)
            Plushies.conf.increment("casino.payout", amount.toDouble())
        }
    }
}