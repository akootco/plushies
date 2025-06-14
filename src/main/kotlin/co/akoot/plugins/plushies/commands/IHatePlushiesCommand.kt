package co.akoot.plugins.plushies.commands

import co.akoot.plugins.bluefox.api.FoxCommand
import co.akoot.plugins.bluefox.api.FoxPlugin
import co.akoot.plugins.plushies.util.Items.isPlushie
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.inventory.ItemStack

class IHatePlushiesCommand(plugin: FoxPlugin) : FoxCommand(plugin, "ihateplushies") {

    override fun onTabComplete(sender: CommandSender, alias: String, args: Array<out String>): MutableList<String> {
        return mutableListOf()
    }

    override fun onCommand(sender: CommandSender, alias: String, args: Array<out String>): Boolean {
        val p = playerCheck(sender)?: return false
        val item = p.inventory.itemInMainHand

        if (item.isPlushie) {
            p.inventory.setItemInMainHand(ItemStack(Material.TOTEM_OF_UNDYING))
        } else {
            p.chat("I hate plushies")
        }

        return true
    }
}
