package co.akoot.plugins.plushies.commands

import co.akoot.plugins.bluefox.api.FoxCommand
import co.akoot.plugins.bluefox.api.FoxPlugin
import co.akoot.plugins.plushies.util.builders.ItemBuilder
import io.papermc.paper.datacomponent.DataComponentTypes
import org.bukkit.command.CommandSender
import org.bukkit.Material

class MaceCommand(plugin: FoxPlugin) : FoxCommand(plugin, "mace", description = "Changes the mace to use a 3D model") {

    override fun onTabComplete(sender: CommandSender, args: Array<out String>): MutableList<String> {
        return mutableListOf()
    }

    override fun onCommand(sender: CommandSender, alias: String, args: Array<out String>): Boolean {
        val p = playerCheck(sender) ?: return false

        val item = p.inventory.itemInMainHand
        // i might change this to work for any tool
        // will need to find/make 3D models first
        if (item.type != Material.MACE) {
            sendError(p, "You need to be holding a mace")
            return false
        }

        if (item.itemMeta.hasCustomModelData()) {
            ItemBuilder.builder(item)
                .customModelData(999)
                .build()
        } else {
            ItemBuilder.builder(item)
                .unsetData(DataComponentTypes.CUSTOM_MODEL_DATA)
                .build()
        }

        return true
    }
}