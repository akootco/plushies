package co.akoot.plugins.plushies.coolstuff.atm

import co.akoot.plugins.bluefox.api.dialog
import co.akoot.plugins.bluefox.api.economy.Coin
import co.akoot.plugins.bluefox.api.economy.Market
import co.akoot.plugins.bluefox.extensions.getPDC
import co.akoot.plugins.bluefox.extensions.setPDC
import co.akoot.plugins.bluefox.extensions.wallet
import co.akoot.plugins.bluefox.util.parse
import co.akoot.plugins.bluefox.util.text
import co.akoot.plugins.bluefox.util.zip
import co.akoot.plugins.plushies.Plushies.Companion.key
import io.papermc.paper.dialog.Dialog
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import java.math.BigDecimal

private var Entity.fee: Float
    get() = getPDC<Float>(key("atm.fee")) ?: 0f
    set(value) = setPDC(key("atm.fee"), value)

private var Entity.title: String
    get() = getPDC<String>(key("atm.name")) ?: ""
    set(value) = setPDC(key("atm.name"), value)

object ATMMenu {

    fun settingsMenu(atm: Entity): Dialog {
        return dialog {
            title("ATM Settings".text)
            closeWithEscape(true)

            textInput(200, "name", "Name".text, atm.title, 4)

            slider(200, "fee", "Fee Percentage".text, 0f..100f, atm.fee)

            button(75, "Save".text) { p, view ->
                val name = view.getText("name") ?: ""
                val fee = view.getFloat("fee") ?: 0f

                atm.fee = fee
                atm.title = name
            }
        }
    }

    fun mainMenu(atm: Entity): Dialog {
        return dialog {
            title(atm.title.parse())
            closeWithEscape(true)

            for (coin in Market.coins.filter { it.value.backing != null }.values) {
                button(100, text("$", coin.ticker).zip) { p, _ ->
                    p.showDialog(coinMenu(atm, p, coin))
                }
            }
        }
    }

    fun coinMenu(atm: Entity, player: Player, coin: Coin): Dialog {
        return dialog {
            title(coin.name.text)
            closeWithEscape(true)

            val balance = player.wallet?.balance[coin] ?: BigDecimal.ZERO

            message(text("Balance: ", balance).zip)

            textInput(100, "amount", "Withdraw Amount".text)

            button(75, "Go Back".text) { p, _ ->
                p.showDialog(mainMenu(atm))
            }

            button(75, "Confirm".text) { p, view ->
                TODO()
            }
        }
    }
}