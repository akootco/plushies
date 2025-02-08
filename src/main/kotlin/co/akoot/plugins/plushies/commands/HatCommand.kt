package co.akoot.plugins.plushies.commands

import co.akoot.plugins.bluefox.api.FoxCommand
import co.akoot.plugins.bluefox.api.FoxPlugin
import co.akoot.plugins.plushies.util.builders.EquippableBuilder
import co.akoot.plugins.plushies.util.builders.ItemBuilder
import io.papermc.paper.datacomponent.DataComponentTypes
import org.bukkit.command.CommandSender
import org.bukkit.NamespacedKey
import org.bukkit.inventory.EquipmentSlot

class HatCommand(plugin: FoxPlugin) : FoxCommand(plugin, "hat") {

    override fun onTabComplete(sender: CommandSender, alias: String, args: Array<out String>): MutableList<String> {
        return mutableListOf()
    }

    override fun onCommand(sender: CommandSender, alias: String, args: Array<out String>): Boolean {
        val p = playerCheck(sender) ?: return false
        val item = p.inventory.itemInMainHand

        if (item.isEmpty) {
            return sendError(p, "You need to hold something")
        }

        val hatKey = NamespacedKey(plugin, "hat")

        if (item.persistentDataContainer.has(hatKey)) {
            ItemBuilder.builder(item)
                .removepdc(hatKey)
                .unsetData(DataComponentTypes.EQUIPPABLE)
                .build()
        } else {
            ItemBuilder.builder(EquippableBuilder.builder(item, EquipmentSlot.HEAD).build())
                .pdc(hatKey)
                .build()
        }

        return true
    }
}