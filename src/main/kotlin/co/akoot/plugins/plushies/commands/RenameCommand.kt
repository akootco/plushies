package co.akoot.plugins.plushies.commands

import co.akoot.plugins.bluefox.api.FoxCommand
import co.akoot.plugins.bluefox.api.FoxPlugin
import co.akoot.plugins.plushies.util.builders.ItemBuilder
import net.kyori.adventure.text.TextReplacementConfig
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.Material
import org.bukkit.command.CommandSender

class RenameCommand(plugin: FoxPlugin) : FoxCommand(plugin, "rename") {

    override fun onTabComplete(sender: CommandSender, args: Array<out String>): MutableList<String> {
        return mutableListOf()
    }

    override fun onCommand(sender: CommandSender, alias: String, args: Array<out String>): Boolean {
        val p = playerCheck(sender) ?: return false

        if (args.isEmpty()) {
            sendError(p, "You must provide a name.")
            return false
        }

        val item = p.inventory.itemInMainHand

        if (item.type == Material.AIR) {
            sendError(p, "You must hold something.")
            return false
        }

        val newName = LegacyComponentSerializer.legacyAmpersand()
            .deserialize(args.joinToString(" ").replace("&&", "*&#&*"))

        // erm
        val replace = TextReplacementConfig.builder()
            .matchLiteral("*&#&*")
            .replacement("&")
            .build()

        ItemBuilder.builder(item)
            .itemName(newName.replaceText(replace))
            .build()

        return true
    }
}

