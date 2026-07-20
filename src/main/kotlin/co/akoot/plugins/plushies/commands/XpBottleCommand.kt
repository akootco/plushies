package co.akoot.plugins.plushies.commands

import co.akoot.plugins.bluefox.api.FoxCommand
import co.akoot.plugins.bluefox.api.FoxPlugin
import co.akoot.plugins.bluefox.util.text
import co.akoot.plugins.plushies.util.Items.xpBottle
import org.bukkit.Material
import org.bukkit.command.CommandSender

class XpBottleCommand(plugin: FoxPlugin) : FoxCommand(plugin, "xpbottle") {
    override fun onTabComplete(sender: CommandSender, alias: String, args: Array<out String>): MutableList<String> = mutableListOf()

    override fun onCommand(sender: CommandSender, alias: String, args: Array<out String>): Boolean {
        val p = playerCheck(sender) ?: return false
        val hand = p.inventory.itemInMainHand

        if (hand.type != Material.EXPERIENCE_BOTTLE) {
            return sendError(sender, "Whatever you are holding cannot store xp!")
        }

        val bottle = hand.asOne() //dupe

        bottle.xpBottle = (bottle.xpBottle ?: 0) + p.calculateTotalExperiencePoints()

        p.apply {
            hand.amount--
            setExperienceLevelAndProgress(0)
            give(bottle)
        }

        p.sendActionBar(text("Nice!"))
        return true
    }
}
