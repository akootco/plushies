package co.akoot.plugins.plushies.coolstuff.casino.games

import co.akoot.plugins.bluefox.api.dialog
import co.akoot.plugins.bluefox.extensions.getPDCList
import co.akoot.plugins.bluefox.extensions.playSound
import co.akoot.plugins.bluefox.extensions.setPDC
import co.akoot.plugins.bluefox.util.*
import co.akoot.plugins.plushies.Plushies.Companion.key
import co.akoot.plugins.plushies.coolstuff.casino.Casino
import co.akoot.plugins.plushies.coolstuff.casino.Casino.payout
import co.akoot.plugins.plushies.coolstuff.casino.CasinoGame
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

object MinesDealer : CasinoGame {
    override val key = key("casino.mines")
    override val displayName = "Mines"

    override fun interact(entity: Entity, player: Player) {
        if (player.isInsideVehicle) player.showDialog(mainMenu(player))
        else player.sendActionBar("Please take a seat".error)
    }

    private var Player.mineGameSettings: List<Float>?
        get() = getPDCList(key)
        set(value) = setPDC(key, value)

    private fun mainMenu(p: Player): Dialog = dialog {
        closeWithEscape(true)
        title("Mines".text)
        columns(2)

        message(
            text(
                "How to Play Mines\n\n",
                "Choose your ", "bet".primary,
                " and ", "mine count".primary,
                ".\n",
                "More mines = ", "higher risk".primary,
                " and ", "bigger payouts".primary,
                "."
            ).zip
        )

        val preset = p.mineGameSettings
        val defaultMines = preset?.getOrNull(1) ?: 1f
        val defaultBet = preset?.getOrNull(0) ?: 100f


        slider(
            150,
            "mines",
            "Mines".text,
            1f..24f,
            defaultMines
        )

        slider(
            150,
            "bet",
            "Bet".text,
            50f..500f,
            defaultBet,
            50f
        )

        toggle("save", "save settings".text, preset?.isNotEmpty() == true)

        button(150, "Play!".text) { p, view ->
            val bet = view.getFloat("bet") ?: 0f
            val mineCount = (view.getFloat("mines") ?: 1f).toInt()
            val savePreset = view.getBoolean("save")

            p.mineGameSettings = if (savePreset == true)
                listOf(bet, mineCount.toFloat()) else null

            val mines = (1..25).shuffled().take(mineCount)
            val game = MinesGame(mines, mineCount, bet)

            Casino.takePayment(p, bet, "Mines") {
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
            title("Mines".text)

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