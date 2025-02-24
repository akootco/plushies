package co.akoot.plugins.plushies.commands

import co.akoot.plugins.bluefox.api.FoxCommand
import co.akoot.plugins.bluefox.api.FoxPlugin
import co.akoot.plugins.plushies.listeners.tasks.Golf.Companion.golfBalls
import co.akoot.plugins.plushies.listeners.tasks.Golf.Companion.golfKey
import co.akoot.plugins.plushies.util.builders.ItemBuilder
import org.bukkit.command.CommandSender

class GolfCommand(plugin: FoxPlugin) : FoxCommand(plugin, "golf") {

    override fun onTabComplete(sender: CommandSender, alias: String, args: Array<out String>): MutableList<String> {

        return if (args.size == 1) arrayListOf(
            "white",
            "purple",
            "yellow",
            "pink",
            "lime",
            "blue",
            "red",
            "green",
            "orange"
        ) else mutableListOf()
    }

    override fun onCommand(sender: CommandSender, alias: String, args: Array<out String>): Boolean {
        val p = playerCheck(sender) ?: return false

        val item = p.inventory.itemInMainHand

        if (!item.type.isBlock) return sendError(p, "You need to be holding a block")

        val isGolfBall = item.persistentDataContainer.has(golfKey)
        val b = ItemBuilder.builder(item)

        if (args.size == 1) {
            val color = if (args[0] in golfBalls) args[0] else "white"
            b.pdc(golfKey, color).build()
            return sendMessage(p, "Color set to $color")
        }

        if (args.isEmpty()) {
            if (isGolfBall) b.removepdc(golfKey).build()
            else b.pdc(golfKey, "white").build()

            return sendMessage(
                p, item.type.name.lowercase().replace("_", " ")
                        + " is ${if (!isGolfBall) "now" else "no longer"} a golf ball"
            )
        }

        return false
    }
}