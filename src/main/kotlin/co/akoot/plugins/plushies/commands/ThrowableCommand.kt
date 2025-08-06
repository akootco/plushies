package co.akoot.plugins.plushies.commands

import co.akoot.plugins.bluefox.api.FoxCommand
import co.akoot.plugins.bluefox.api.FoxPlugin
import co.akoot.plugins.bluefox.api.Kolor
import co.akoot.plugins.bluefox.util.Text.Companion.cleanName
import co.akoot.plugins.bluefox.util.Text.Companion.now
import co.akoot.plugins.plushies.listeners.tasks.Throwable.Companion.axeKey
import co.akoot.plugins.plushies.util.builders.ItemBuilder
import com.destroystokyo.paper.MaterialTags
import io.papermc.paper.datacomponent.DataComponentTypes
import org.bukkit.command.CommandSender

class ThrowableCommand(plugin: FoxPlugin) : FoxCommand(plugin, "throwable") {

    override fun onTabComplete(sender: CommandSender, alias: String, args: Array<out String>): MutableList<String> {
        return mutableListOf()
    }

    override fun onCommand(sender: CommandSender, alias: String, args: Array<out String>): Boolean {
        val p = playerCheck(sender) ?: return false
        val item = p.inventory.itemInMainHand
        val isThrowable = item.persistentDataContainer.has(axeKey)

        if (item.isEmpty ||MaterialTags.THROWABLE_PROJECTILES.isTagged(item)) return false

        ItemBuilder.builder(item).apply {
            if (isThrowable) removepdc(axeKey).resetData(DataComponentTypes.FOOD).resetData(DataComponentTypes.CONSUMABLE)
            else throwable()
        }

        return Result.success(Kolor.ACCENT(item.type.cleanName) + Kolor.TEXT(" is ${isThrowable.not().now} throwable")).getAndSend(p)
    }
}