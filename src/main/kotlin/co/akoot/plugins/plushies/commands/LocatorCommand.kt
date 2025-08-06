package co.akoot.plugins.plushies.commands
import co.akoot.plugins.bluefox.api.FoxCommand
import co.akoot.plugins.bluefox.api.FoxPlugin
import org.bukkit.command.CommandSender
import org.bukkit.attribute.Attribute

class LocatorCommand(plugin: FoxPlugin) : FoxCommand(plugin, "locator", "toggles locator bar") {

    override fun onTabComplete(sender: CommandSender, alias: String, args: Array<out String>): MutableList<String> {
        return mutableListOf("on", "off")
    }

    override fun onCommand(sender: CommandSender, alias: String, args: Array<out String>): Boolean {
        val p = playerCheck(sender) ?: return false
        val receive = p.getAttribute(Attribute.WAYPOINT_RECEIVE_RANGE)
        val transmit = p.getAttribute(Attribute.WAYPOINT_TRANSMIT_RANGE)

        val value = when (args.getOrNull(0)) {
            "on"  -> 6.0e7 // good one
            "off" -> 0.0
            else  -> if (receive?.baseValue == 0.0) 6.0e7 else 0.0
        }

        receive?.baseValue = value
        transmit?.baseValue = value
        return true
    }
}