package co.akoot.plugins.plushies.commands

import co.akoot.plugins.bluefox.api.FoxCommand
import co.akoot.plugins.bluefox.api.FoxPlugin
import co.akoot.plugins.bluefox.api.economy.Market
import co.akoot.plugins.plushies.gui.atm.ATMMenu
import co.akoot.plugins.plushies.gui.atm.CoinMenu
import co.akoot.plugins.plushies.util.Util.inValidWorld
import org.bukkit.command.CommandSender

class ATMCommand(plugin: FoxPlugin) : FoxCommand(plugin, "atm") {

    override fun onTabComplete(sender: CommandSender, alias: String, args: Array<out String>): MutableList<String> {
        if (args.size == 1) {
            return Market.coins.filter { it.value.backing != null }.keys.toMutableList()
        }
        return mutableListOf()
    }

    override fun onCommand(sender: CommandSender, alias: String, args: Array<out String>): Boolean {
        val p = playerCheck(sender) ?: return false
        if (!p.inValidWorld(this)) return false

        val coin = Market.getCoin(args.getOrNull(0) ?: "")

        p.openInventory(
            if (coin != null) CoinMenu(p, coin).inventory
            else ATMMenu(p).inventory
        )

        return true
    }
}