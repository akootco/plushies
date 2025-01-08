package co.akoot.plugins.plushies.commands

import co.akoot.plugins.bluefox.api.FoxCommand
import co.akoot.plugins.bluefox.api.FoxPlugin
import co.akoot.plugins.bluefox.util.Txt
import co.akoot.plugins.plushies.util.ItemBuilder
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.command.CommandSender

class ThrowableCommand(plugin: FoxPlugin) : FoxCommand(plugin, "throwable") {

    override fun onTabComplete(sender: CommandSender, args: Array<out String>): MutableList<String> {
        if (args.size == 1 && hasPermission(sender, "all")) return mutableListOf("smite")
        return mutableListOf()
    }

    override fun onCommand(sender: CommandSender, alias: String, args: Array<out String>): Boolean {
        val p = playerCheck(sender) ?: return false

        val item = p.inventory.itemInMainHand

        // not even sure why i am restricting this to axes for normal player
        if (!hasPermission(sender, "all") && !item.type.name.endsWith("_AXE")) {
            sendError(p, "You need to be holding an axe")
            return false
        }

        // Only add lightning effect if the player asked for it
        // but most importantly, check if they are cool enough
        val smite = args.getOrNull(0) == "smite" && hasPermission(sender, "all")

        ItemBuilder.builder(item)
            .throwable(smite) // cool stuff!
            .build()

        p.sendMessage((Txt()
                + Txt(item.type.name.lowercase().replace("_", " ")).color("accent")
                + Txt(" is now throwable")
                + Txt(if (smite) " (lightning)" else "").color(NamedTextColor.GRAY)
                ).c)

        return true
    }
}