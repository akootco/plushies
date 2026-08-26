package co.akoot.plugins.plushies.coolstuff.casino.games

import co.akoot.plugins.bluefox.api.dialog
import co.akoot.plugins.bluefox.util.text
import co.akoot.plugins.plushies.Plushies.Companion.key
import co.akoot.plugins.plushies.coolstuff.casino.CasinoGame
import io.papermc.paper.dialog.Dialog
import org.bukkit.entity.Entity
import org.bukkit.entity.Player

object Lottery : CasinoGame {
    override val key = key("casino.lotto")
    override val displayName = "Lottery"

    override fun interact(entity: Entity, player: Player) {
        player.showDialog(mainMenu())
    }

    fun mainMenu(): Dialog = dialog {
        closeWithEscape(true)
        title("Pick your numbers".text)
        columns(5)

        cancelButton()
    }
}