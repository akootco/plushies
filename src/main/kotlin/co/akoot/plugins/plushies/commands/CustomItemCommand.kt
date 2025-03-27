package co.akoot.plugins.plushies.commands

import co.akoot.plugins.bluefox.api.FoxCommand
import co.akoot.plugins.bluefox.api.FoxPlugin
import co.akoot.plugins.plushies.Plushies.Companion.customItemConfig
import co.akoot.plugins.plushies.gui.CustomItemMenu
import co.akoot.plugins.plushies.util.Items.customItems
import co.akoot.plugins.plushies.util.Items.loadItems
import co.akoot.plugins.plushies.util.Items.partyHat
import org.bukkit.command.CommandSender

class CustomItemCommand(plugin: FoxPlugin) : FoxCommand(plugin, "customitem") {

    override fun onTabComplete(sender: CommandSender, alias: String, args: Array<out String>): MutableList<String> {

        if (args.size == 1) {
            return customItems.keys.toMutableList()
        } else if (args.size == 2 && args[0] == "party_hat") {
            return getOnlinePlayerSuggestions()
        }

        return mutableListOf()
    }

    override fun onCommand(sender: CommandSender, alias: String, args: Array<out String>): Boolean {
        val p = playerCheck(sender) ?: return false

        when (args.getOrNull(0)) {
            "reload" -> {
                customItems.clear()
                loadItems(customItemConfig)
                return sendMessage(sender, "Custom items reloaded")
            }

            "party_hat" -> {
                val item = partyHat(args.getOrNull(1) ?: "")
                val count = args.getOrNull(2)?.toIntOrNull() ?: 1
                p.inventory.addItem(item.clone().apply { amount = count })
                return true
            }

            else -> {
                if (args.isEmpty()) {
                    p.openInventory(CustomItemMenu().inventory)
                    return true
                }

                val outputItem = customItems.keys.find { it.equals(args[0], ignoreCase = true) }
                    ?.let { customItems[it] } ?: run { return sendError(sender, "Invalid item.") }
                val count = args.getOrNull(2)?.toIntOrNull() ?: 1

                p.inventory.addItem(outputItem.clone().apply { amount = count })
                return true
            }
        }
    }
}