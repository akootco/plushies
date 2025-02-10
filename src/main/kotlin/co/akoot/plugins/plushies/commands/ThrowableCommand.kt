package co.akoot.plugins.plushies.commands

import co.akoot.plugins.bluefox.api.FoxCommand
import co.akoot.plugins.bluefox.api.FoxPlugin
import co.akoot.plugins.bluefox.util.Text
import co.akoot.plugins.plushies.listeners.tasks.Throwable.Companion.axeKey
import co.akoot.plugins.plushies.util.builders.ItemBuilder
import org.bukkit.command.CommandSender

class ThrowableCommand(plugin: FoxPlugin) : FoxCommand(plugin, "throwable") {

    override fun onTabComplete(sender: CommandSender, args: Array<out String>): MutableList<String> {
        return mutableListOf()
    }

    override fun onCommand(sender: CommandSender, alias: String, args: Array<out String>): Boolean {
        val p = playerCheck(sender) ?: return false

        val item = p.inventory.itemInMainHand

        // not even sure why i am restricting this to axes for normal player
        if (!hasPermission(sender, "all") && !item.type.name.endsWith("_AXE")) {
            return sendError(p, "You need to be holding an axe")
        } else if (item.isEmpty) {
            return sendError(p, "You can't throw [AIR], check out wind charges.")
        }

        val isThrowable = item.persistentDataContainer.has(axeKey)
        val b = ItemBuilder.builder(item)

        if (isThrowable) b.removepdc(axeKey).build()

        p.sendMessage((Text(item.type.name.lowercase().replace("_", " ")).color("accent")
                + Text(" is ${if (!isThrowable) "now" else "no longer"} throwable").color("text")).component)
        return true
    }
}