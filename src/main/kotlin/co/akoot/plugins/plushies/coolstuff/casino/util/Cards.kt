package co.akoot.plugins.plushies.coolstuff.casino.util

import net.kyori.adventure.key.Key
import net.kyori.adventure.text.Component

val hiddenCard = Component.text("1").font(Key.key("casino:cards/back"))
fun Card.display(): Component = Component.text(char).font(font)

data class Card(
    val char: Char,
    val font: Key
)

fun fresh52CardDeck(): MutableList<Card> {
    val chars = "A23456789XJQK"

    val suits = listOf(
        Key.key("casino:cards/spades"),
        Key.key("casino:cards/hearts"),
        Key.key("casino:cards/clubs"),
        Key.key("casino:cards/diamonds")
    )

    return mutableListOf<Card>().apply {
        for (suit in suits) {
            for (char in chars) {
                add(Card(char, suit))
            }
        }

       repeat(4) { shuffle() }
    }
}