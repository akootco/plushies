package co.akoot.plugins.plushies.coolstuff.casino.util

import co.akoot.plugins.bluefox.api.DialogBuilder
import co.akoot.plugins.bluefox.extensions.getPDC
import co.akoot.plugins.bluefox.extensions.setPDC
import co.akoot.plugins.bluefox.util.text
import co.akoot.plugins.plushies.Plushies.Companion.key
import io.papermc.paper.dialog.DialogResponseView
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player


private var Player.preferredBet: Float?
    get() = getPDC(key("casino.bet"))
    set(value) = setPDC(key("casino.bet"), value)

fun DialogBuilder.betSlider(player: Player) {
    slider(
        150,
        "bet",
        "Bet".text,
        100f..500f,
        player.preferredBet ?: 100f,
        50f
    )
}

fun DialogBuilder.playButton(
    label: Component,
    action: (Player, Float, DialogResponseView) -> Unit
) = button(label) { player, view ->
    val bet = view.getFloat("bet") ?: 100f

    player.preferredBet = bet
    action(player, bet, view)
}