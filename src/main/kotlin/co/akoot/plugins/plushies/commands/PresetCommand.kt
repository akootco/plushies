package co.akoot.plugins.plushies.commands

import co.akoot.plugins.bluefox.api.FoxCommand
import co.akoot.plugins.bluefox.api.FoxPlugin
import co.akoot.plugins.plushies.Plushies.Companion.key
import co.akoot.plugins.plushies.util.builders.EnchantPreset
import co.akoot.plugins.plushies.util.builders.ItemBuilder
import org.bukkit.Material
import org.bukkit.Tag
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.command.CommandSender
import org.bukkit.inventory.EquipmentSlotGroup

class PresetCommand(plugin: FoxPlugin) : FoxCommand(plugin, "preset") {

    override fun onTabComplete(sender: CommandSender, alias: String, args: Array<out String>): MutableList<String> {
        val p = playerCheck(sender) ?: return mutableListOf()
        val item = p.inventory.itemInMainHand
        val hasDigTool = Tag.ITEMS_AXES.isTagged(item.type) ||
                Tag.ITEMS_PICKAXES.isTagged(item.type) ||
                Tag.ITEMS_HOES.isTagged(item.type) ||
                Tag.ITEMS_SHOVELS.isTagged(item.type)
        return when {
            args.size == 1 && hasDigTool -> {
                arrayListOf("silk", "fortune")
            }

            args.size == 1 && item.type == Material.ELYTRA -> {
                arrayListOf("armored")
            }

            else -> mutableListOf()
        }
    }

    override fun onCommand(sender: CommandSender, alias: String, args: Array<out String>): Boolean {
        val p = playerCheck(sender) ?: return false
        val item = p.inventory.itemInMainHand

        if (item.type == Material.AIR) {
            return sendError(p, "You must hold something.")
        }

        when {
            Tag.ITEMS_AXES.isTagged(item.type) -> {
                EnchantPreset.builder(item)
                    .axe(args.getOrNull(1) == "fortune")
                    .build()
                return true
            }

            Tag.ITEMS_SWORDS.isTagged(item.type) -> {
                EnchantPreset.builder(item)
                    .sword()
                    .build()
                return true
            }

            Tag.ITEMS_PICKAXES.isTagged(item.type) ||
                    Tag.ITEMS_HOES.isTagged(item.type) ||
                    Tag.ITEMS_SHOVELS.isTagged(item.type) -> {
                EnchantPreset.builder(item)
                    .digTool(args.getOrNull(1) == "fortune")
                    .build()
                return true
            }

            Tag.ITEMS_HEAD_ARMOR.isTagged(item.type) -> {
                EnchantPreset.builder(item)
                    .helmet()
                    .build()
                return true
            }

            Tag.ITEMS_LEG_ARMOR.isTagged(item.type) -> {
                EnchantPreset.builder(item)
                    .leggings()
                    .build()
                return true
            }

            Tag.ITEMS_FOOT_ARMOR.isTagged(item.type) -> {
                EnchantPreset.builder(item)
                    .boots()
                    .build()
                return true
            }

            Tag.ITEMS_CHEST_ARMOR.isTagged(item.type) -> {
                EnchantPreset.builder(item)
                    .chestplate()
                    .build()
                return true
            }

            item.type == Material.ELYTRA -> {
                if (args.getOrNull(0) == "armored") {
                    ItemBuilder.builder(item)
                        .attribute(
                            Attribute.ARMOR,
                            AttributeModifier(key("elytra_armor"), 8.0, AttributeModifier.Operation.ADD_NUMBER),
                            EquipmentSlotGroup.CHEST
                        ).build()
                }

                EnchantPreset.builder(item).build()
                return true
            }

            else -> {
                return sendError(p,"This item does not have an enchantment preset!")
            }
        }
    }
}

