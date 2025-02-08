package co.akoot.plugins.plushies.commands

import co.akoot.plugins.bluefox.api.FoxCommand
import co.akoot.plugins.bluefox.api.FoxPlugin
import co.akoot.plugins.plushies.util.builders.EnchantPreset
import org.bukkit.Material
import org.bukkit.command.CommandSender

class PresetCommand(plugin: FoxPlugin) : FoxCommand(plugin, "preset") {

    override fun onTabComplete(sender: CommandSender, alias: String, args: Array<out String>): MutableList<String> {

        if (args.size == 1) return arrayListOf(
            "axe",
            "sword",
            "chestplate",
            "leggings",
            "helmet",
            "dig",
            "boots"
        ) else if (args.size == 2 && args[0] in listOf("axe", "dig")) {
            return arrayListOf("fortune")
        }

        return mutableListOf()
    }

    override fun onCommand(sender: CommandSender, alias: String, args: Array<out String>): Boolean {
        val p = playerCheck(sender) ?: return false

        if (args.isEmpty()) {
            return sendError(p, "You must define a preset.")
        }

        val item = p.inventory.itemInMainHand

        if (item.type == Material.AIR) {
            return sendError(p, "You must hold something.")
        }

        when (args[0]) {
            "axe" -> {
                EnchantPreset.builder(item)
                    .axe(args.getOrNull(1) == "fortune")
                    .build()
                return true
            }

            "sword" -> {
                EnchantPreset.builder(item)
                    .sword()
                    .build()
                return true
            }

            "dig" -> {
                EnchantPreset.builder(item)
                    .digTool(args.getOrNull(1) == "fortune")
                    .build()
                return true
            }

            "helmet" -> {
                EnchantPreset.builder(item)
                    .helmet()
                    .build()
                return true
            }

            "leggings" -> {
                EnchantPreset.builder(item)
                    .leggings()
                    .build()
                return true
            }

            "boots" -> {
                EnchantPreset.builder(item)
                    .boots()
                    .build()
                return true
            }

            "chestplate" -> {
                EnchantPreset.builder(item)
                    .chestplate()
                    .build()
                return true
            }

            else -> {
                return sendError(p,"Preset: '${args[0]}' does not exist!")
            }
        }
    }
}

