package co.akoot.plugins.plushies.commands

import co.akoot.plugins.bluefox.api.FoxCommand
import co.akoot.plugins.bluefox.api.FoxPlugin
import co.akoot.plugins.plushies.util.builders.ItemBuilder
import io.papermc.paper.datacomponent.DataComponentTypes
import net.kyori.adventure.text.TextReplacementConfig
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.Material
import org.bukkit.command.CommandSender

class LoreCommand(plugin: FoxPlugin) : FoxCommand(plugin, "lore") {

    override fun onTabComplete(sender: CommandSender, args: Array<out String>): MutableList<String> {
        return mutableListOf()
    }

    override fun onCommand(sender: CommandSender, alias: String, args: Array<out String>): Boolean {
        val p = playerCheck(sender) ?: return false

        if (args.isEmpty()) {
            sendError(p, "Lore cannot be empty.")
            return false
        }

        val item = p.inventory.itemInMainHand

        if (item.type == Material.AIR) {
            sendError(p, "You must hold something.")
            return false
        }

        // clear lore
        if (args[0] == "-c") {
            ItemBuilder.builder(item)
                .unsetData(DataComponentTypes.LORE)
                .build()

            sendMessage(p, "Lore cleared.")
            return true
        }

        // what the freak man
        val lore = args.joinToString(" ").replace("&&", "*&#&*").split("\\n").map {
            LegacyComponentSerializer.legacyAmpersand().deserialize(it)
        }

        val replace = TextReplacementConfig.builder()
            .matchLiteral("*&#&*")
            .replacement("&")
            .build()

        ItemBuilder.builder(item)
            .lore(lore.map { it.replaceText(replace) })
            .build()

        return true
    }
}

