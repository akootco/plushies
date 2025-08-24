package co.akoot.plugins.plushies.commands

import co.akoot.plugins.bluefox.api.FoxCommand
import co.akoot.plugins.bluefox.api.FoxPlugin
import co.akoot.plugins.plushies.Plushies.Companion.key
import co.akoot.plugins.plushies.util.Items.isCustomItem
import co.akoot.plugins.plushies.util.Items.isPlushie
import co.akoot.plugins.plushies.util.builders.EquippableBuilder
import co.akoot.plugins.plushies.util.builders.ItemBuilder
import io.papermc.paper.datacomponent.DataComponentTypes
import org.bukkit.Material
import org.bukkit.command.CommandSender

class ToggleArmorCommand(plugin: FoxPlugin) :
    FoxCommand(plugin, "togglearmor", "toggle visibility of armor", aliases = arrayOf("armor")) {

    override fun onTabComplete(sender: CommandSender, alias: String, args: Array<out String>): MutableList<String> {
        return mutableListOf()
    }

    override fun onCommand(sender: CommandSender, alias: String, args: Array<out String>): Boolean {
        val p = playerCheck(sender) ?: return false

        p.inventory.armorContents.forEach { item ->
            if (item == null || item.type == Material.ELYTRA || item.isCustomItem || item.isPlushie ) return@forEach

            if (item.persistentDataContainer.has(key("hidden_armor"))) {
                ItemBuilder.builder(item)
                    .resetData(DataComponentTypes.EQUIPPABLE)
                    .removepdc(key("hidden_armor"))
                    .build()
            } else {
                val armor = EquippableBuilder.builder(item, item.type.equipmentSlot)
                ItemBuilder.builder(armor.model(key("hidden_armor")).build())
                    .pdc(key("hidden_armor"), true).build()
            }
        }
        return true
    }
}
