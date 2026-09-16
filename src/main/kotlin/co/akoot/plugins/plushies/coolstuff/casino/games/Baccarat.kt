package co.akoot.plugins.plushies.coolstuff.casino.games

import co.akoot.plugins.bluefox.api.dialog
import co.akoot.plugins.bluefox.api.px
import co.akoot.plugins.bluefox.util.error
import co.akoot.plugins.bluefox.util.runLater
import co.akoot.plugins.bluefox.util.text
import co.akoot.plugins.bluefox.util.zip
import co.akoot.plugins.plushies.Plushies.Companion.key
import co.akoot.plugins.plushies.coolstuff.casino.util.Card
import co.akoot.plugins.plushies.coolstuff.casino.util.Casino
import co.akoot.plugins.plushies.coolstuff.casino.util.CasinoGame
import co.akoot.plugins.plushies.coolstuff.casino.util.display
import co.akoot.plugins.plushies.coolstuff.casino.util.fresh52CardDeck
import co.akoot.plugins.plushies.coolstuff.casino.util.betScreen
import co.akoot.plugins.plushies.coolstuff.casino.util.playButton
import co.akoot.plugins.plushies.coolstuff.casino.util.spacer
import co.akoot.plugins.plushies.coolstuff.casino.util.value
import net.kyori.adventure.key.Key
import net.kyori.adventure.text.Component
import org.bukkit.entity.Entity
import org.bukkit.entity.Player

private val List<Card>.value: Int
    get() = value(1, 0) % 10

data class BaccaratGame(
    var bet: Float,
    val deck: MutableList<Card> = fresh52CardDeck(),
    val playerHand: MutableList<Card> = mutableListOf(),
    val bankerHand: MutableList<Card> = mutableListOf(),
    val choice: String,
    var state: BaccaratState = BaccaratState.SETUP
)

enum class BaccaratState {
    // smh
    SETUP,
    DEAL_PLAYER,
    DEAL_BANKER,
    CHECK,
    PLAYER_DRAW,
    BANKER_DRAW,
    PLAYER,
    BANKER,
    TIE
}

object Baccarat : CasinoGame {
    override val key = key("casino.baccarat")
    override val displayName = "Baccarat"

    override fun interact(entity: Entity, player: Player) {
        if (player.isInsideVehicle) player.betScreen(player)
        else player.sendActionBar("Please take a seat".error)
    }

    private fun Player.betScreen(p: Player) {
        showDialog(
            dialog {
                spacer(8)
                betScreen(p, this@Baccarat.displayName.text)
                for (choice in listOf("Banker", "Player", "Tie")) {
                    playButton(choice.text) { p, bet, _ ->
                        Casino.takePayment(p, bet, this@Baccarat.displayName) {
                            runLater(15) {
                                p.showGame(BaccaratGame(bet, choice = choice))
                            }
                        }
                    }
                }
            }
        )
    }


    private fun Player.showGame(game: BaccaratGame) {
        showDialog(dialog {
            columns(2)

            title("1".text.font(Key.key("casino:baccarat")))

            gameLogic(this@showGame, game)

            spacer(4)

            message(200,
                text(
                    game.bankerHand.fold(Component.newline()) { component, card ->
                        component.append(card.display()).appendSpace()
                    }
                ).zip
            )

            message(text("\n", game.bankerHand.value).zip)

            spacer(3)

            message(200,
                text(
                    game.playerHand.fold(Component.newline()) { component, card ->
                        component.append(card.display()).appendSpace()
                    }
                ).zip
            )

            message(text("\n", game.playerHand.value).zip)

            val message = when (game.state){
                BaccaratState.PLAYER -> text("\nPlayer Wins")
                BaccaratState.BANKER -> text("\nBanker Wins")
                BaccaratState.TIE -> text("\nTIE ")
                else -> text("\nCurrent bet: ", game.choice).zip
            }

            message(message)
        })
    }

    fun gameLogic(player: Player, game: BaccaratGame) {
        when (game.state) {
            BaccaratState.SETUP -> {
                // pause for dramatic effect
                game.state = BaccaratState.DEAL_PLAYER
                runLater(25) { player.showGame(game) }
            }

            BaccaratState.DEAL_PLAYER -> {
                game.playerHand += game.deck.removeFirst()
                game.state = BaccaratState.DEAL_BANKER
                runLater(25) { player.showGame(game) }
            }

            BaccaratState.DEAL_BANKER -> {
                game.bankerHand += game.deck.removeFirst()

                if (game.bankerHand.size == 2) {
                    game.state = BaccaratState.CHECK
                } else {
                    game.state = BaccaratState.DEAL_PLAYER
                }
                runLater(25) { player.showGame(game) }
            }

            BaccaratState.CHECK -> {
                if (game.playerHand.value in 8..9 || game.bankerHand.value in 8..9) {
                    checkWinner(game)
                } else {
                    game.state = BaccaratState.PLAYER_DRAW
                }

                runLater(25) { player.showGame(game) }
            }

            BaccaratState.PLAYER_DRAW -> {
                if (game.playerHand.size == 2 && game.playerHand.value <= 5) {
                    game.playerHand += game.deck.removeFirst()
                } else {
                    game.state = BaccaratState.BANKER_DRAW
                }
                runLater(25) { player.showGame(game) }
            }

            BaccaratState.BANKER_DRAW -> {
                if (game.bankerHand.size == 3) return player.showGame(game)

                val shouldDraw = when {
                    game.playerHand.size == 2 -> {
                        game.bankerHand.value <= 5
                    }

                    else -> {
                        val p3rd = game.playerHand.drop(2).value
                        // who tf came up with ts rules bro
                        when (game.bankerHand.value) {
                            in 0..2 -> true
                            3 -> p3rd != 8
                            4 -> p3rd in 2..7
                            5 -> p3rd in 4..7
                            6 -> p3rd in 6..7
                            else -> false
                        }
                    }
                }

                if (shouldDraw && game.bankerHand.size == 2) game.bankerHand += game.deck.removeFirst()
                else checkWinner(game)

                runLater(25) { player.showGame(game) }
            }

            else -> return
        }
    }

    private fun checkWinner(game: BaccaratGame) {
        game.state = when {
            game.playerHand.value > game.bankerHand.value -> BaccaratState.PLAYER
            game.bankerHand.value > game.playerHand.value -> BaccaratState.BANKER
            else -> BaccaratState.TIE
        }
    }
}