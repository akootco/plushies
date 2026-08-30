package co.akoot.plugins.plushies.coolstuff.casino.games

import co.akoot.plugins.bluefox.api.dialog
import co.akoot.plugins.bluefox.extensions.getPDC
import co.akoot.plugins.bluefox.extensions.playSound
import co.akoot.plugins.bluefox.extensions.removePDC
import co.akoot.plugins.bluefox.extensions.setPDC
import co.akoot.plugins.bluefox.util.*
import co.akoot.plugins.plushies.Plushies.Companion.key
import co.akoot.plugins.plushies.coolstuff.casino.util.Casino
import co.akoot.plugins.plushies.coolstuff.casino.util.Casino.payout
import co.akoot.plugins.plushies.coolstuff.casino.util.CasinoGame
import co.akoot.plugins.plushies.coolstuff.casino.util.betScreen
import co.akoot.plugins.plushies.coolstuff.casino.util.playButton
import io.papermc.paper.dialog.Dialog
import net.kyori.adventure.text.Component
import org.bukkit.Sound
import org.bukkit.entity.Entity
import org.bukkit.entity.Player

data class MinesGame(
    val mines: List<Int>,
    val mineCount: Int,
    val bet: Float,
    val revealed: MutableSet<Int> = mutableSetOf(),
    var multiplier: Float = 0f,
    var state: MinesGamestate = MinesGamestate.PLAYING
)

enum class MinesGamestate {
    FIRST,
    PLAYING,
    WIN,
    LOSE
}

object Mines : CasinoGame {
    override val key = key("casino.mines")
    override val displayName = "Mines"

    override fun interact(entity: Entity, player: Player) {
        if (player.isInsideVehicle) player.showDialog(mainMenu(player))
        else player.sendActionBar("Please take a seat".error)
    }

    private var Player.mineGameSettings: Float?
        get() = getPDC<Float>(key)
        set(value) = if (value == null) removePDC(key) else setPDC(key, value)

    private fun mainMenu(p: Player): Dialog = dialog {
        betScreen(p, displayName.text)

        slider(
            150,
            "mines",
            "Mines".text,
            1f..24f,
            p.mineGameSettings ?: 1f
        )

        toggle("save", "save settings".text, p.mineGameSettings != null)

        playButton("Play!".text) { p, bet, view ->
            val mineCount = view.getFloat("mines") ?: 1f
            val save = view.getBoolean("save") ?: false
            val mines = (1..25).shuffled().take(mineCount.toInt())
            val game = MinesGame(mines, mineCount.toInt(), bet)

            p.mineGameSettings = if (save) mineCount else null

            Casino.takePayment(p, bet, displayName) {
                runLater(10) {
                    p.showDialog(game(game.apply {
                        state = MinesGamestate.FIRST
                    }))
                }
            }
        }
    }

    private fun game(game: MinesGame): Dialog {
        return dialog {
            columns(5)
            title(displayName.text)

            val message = when (game.state) {
                MinesGamestate.LOSE -> "YOU LOSE!"
                MinesGamestate.WIN -> $$"YOU WON $${(game.bet * game.multiplier).toInt()} $hopcoin!"
                else -> "Multiplier: ${game.multiplier}x"
            }

            message(200, "$message\n\n\n")

            for (tile in 1..25) {
                val mine = tile in game.mines

                when {
                    tile in game.revealed -> {
                        val sprite = if (mine) sprite("block/tnt_side")
                        else sprite("item/diamond", "items")

                        button(25, sprite) { player, _ ->
                            return@button player.showDialog(game(game))
                        }
                    }

                    else -> {
                        button(25, Component.empty()) { player, _ ->
                            if (mine) {
                                // loser!!
                                game.state = MinesGamestate.LOSE
                                game.revealed.addAll(1..25)

                                player.showDialog(game(game))
                                player.playSound(
                                    Sound.ENTITY_GENERIC_EXPLODE,
                                    0.5f // loud asf for what
                                )
                            } else {
                                game.state = MinesGamestate.PLAYING
                                game.revealed.add(tile)
                                game.multiplier = getMultiplier(game)

                                if (game.mineCount + game.revealed.size == 25) {
                                    game.state = MinesGamestate.WIN
                                    game.revealed.addAll(1..25)

                                    payout(player, game.bet * game.multiplier)
                                    player.showDialog(game(game))
                                    return@button
                                }

                                player.showDialog(game(game))
                                player.playSound(
                                    Sound.BLOCK_NOTE_BLOCK_CHIME,
                                    pitch = 2f
                                )
                            }
                        }
                    }
                }
            }

            when (game.state) {
                MinesGamestate.FIRST -> {}
                MinesGamestate.PLAYING -> {
                    button(120, "Cashout".text) { player, _ ->
                        payout(player, game.bet * game.multiplier)
                    }
                }

                else -> {
                    button(101, "Play Again!".text) { p, _ ->
                        p.showDialog(mainMenu(p))
                    }

                    cancelButton("Quit".error)
                }
            }
        }
    }

    private fun getMultiplier(game: MinesGame): Float {
        var probability = 1.0

        for (i in 0 until game.revealed.size) {
            probability *= (25.0 - game.mineCount - i) / (25.0 - i)
        } // ts is real casino math, dont come for me mojang pls

        return (0.99 / probability).toFloat()
    }
}