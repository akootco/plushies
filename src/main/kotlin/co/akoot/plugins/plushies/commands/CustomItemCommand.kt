package co.akoot.plugins.plushies.commands

import co.akoot.plugins.bluefox.api.FoxCommand
import co.akoot.plugins.bluefox.api.FoxPlugin
import co.akoot.plugins.bluefox.api.Kolor
import co.akoot.plugins.bluefox.extensions.isSurventure
import co.akoot.plugins.bluefox.util.Text
import co.akoot.plugins.plushies.gui.CustomItemMenu
import co.akoot.plugins.plushies.util.DataPack.createDiscItems
import co.akoot.plugins.plushies.util.Items.customItems
import co.akoot.plugins.plushies.util.Items.isCustomItem
import co.akoot.plugins.plushies.util.Items.loadItems
import co.akoot.plugins.plushies.util.builders.ItemBuilder
import org.bukkit.command.CommandSender

class CustomItemCommand(plugin: FoxPlugin) : FoxCommand(plugin, "customitem") {

    override fun onTabComplete(sender: CommandSender, alias: String, args: Array<out String>): MutableList<String> {

        if (args.size == 1) {
            return customItems.filter { it.value.isCustomItem }.keys.toMutableList()
        } else if (args.size == 2 && args[0] == "party_hat") {
            return getOnlinePlayerSuggestions()
        }

        return mutableListOf()
    }

    override fun onCommand(sender: CommandSender, alias: String, args: Array<out String>): Boolean {
        when (args.getOrNull(0)) {
            "reload" -> {
                customItems.entries.removeIf { it.value.isCustomItem }
                loadItems()
                createDiscItems()
                return sendMessage(sender, "Custom items reloaded")
            }

            "party_hat" -> {
                val p = playerCheck(sender)?: return false
                val item = customItems["party_hat"] ?: return sendError(sender, "Invalid item.")
                val hat =
                    if (args.getOrNull(1) != null)
                        ItemBuilder.builder(item.clone())
                            .lore(listOf(Text("Happy Birthday ${args[1]}",
                                Kolor.MONTH).component)).build()
                    else item

                if (!p.isSurventure) p.give(hat)

                return true
            }

            else -> {
                val p = playerCheck(sender)?: return false
                if (args.isEmpty()) {
                    p.openInventory(CustomItemMenu().inventory)
                    return true
                }

                val outputItem = customItems.keys.find { it.equals(args[0], ignoreCase = true) }
                    ?.let { customItems[it] } ?: run { return sendError(sender, "Invalid item.") }
                val count = args.getOrNull(2)?.toIntOrNull() ?: 1

                if (!p.isSurventure) p.give(outputItem.clone().apply { amount = count })

                return true
            }
        }
    }
}