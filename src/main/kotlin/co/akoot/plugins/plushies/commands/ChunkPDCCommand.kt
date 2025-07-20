package co.akoot.plugins.plushies.commands

import co.akoot.plugins.bluefox.api.FoxCommand
import co.akoot.plugins.bluefox.api.FoxPlugin
import co.akoot.plugins.bluefox.api.Kolor
import co.akoot.plugins.bluefox.extensions.removePDC
import co.akoot.plugins.bluefox.util.Text
import org.bukkit.NamespacedKey
import org.bukkit.command.CommandSender

class ChunkPDCCommand(plugin: FoxPlugin) : FoxCommand(plugin, "chunkpdc") {

    override fun onTabComplete(sender: CommandSender, alias: String, args: Array<out String>): MutableList<String> {
        val p = playerCheck(sender) ?: return mutableListOf()
        return when (args.size) {
            1 -> mutableListOf("list", "remove", "removeall")
            2 -> {
                val keys = p.location.chunk.persistentDataContainer.keys
                when(args[0]) {
                    "remove" -> keys.map { "${it.namespace}:${it.key}" }.toMutableList()
                    "removeall" -> keys.map { it.namespace }.toMutableList()
                    else -> mutableListOf()
                }
            }
            else -> mutableListOf()
        }
    }

    override fun onCommand(sender: CommandSender, alias: String, args: Array<out String>): Boolean {
        val p = playerCheck(sender) ?: return false

        when (args.getOrNull(0)) {
            "list" -> {
                Text(p) { Kolor.WARNING(p.location.chunk.persistentDataContainer.keys.toString()) }
                return true
            }

            "remove" -> {
                if (args.size < 2 || !args[1].contains(":"))
                    return sendError(sender, "'namespace:key'")

                val parts = args[1].split(":")
                val namespace = parts[0].lowercase()
                val key = parts[1].lowercase()
                p.location.chunk.removePDC(NamespacedKey(namespace, key))
                return sendMessage(sender, "removed $namespace:$key")
            }

            "removeall" -> {
                if (args.size < 2) {
                    return sendError(sender, "good try! pick a namespace.")
                }
                p.location.chunk.persistentDataContainer.keys.forEach { key ->
                    if (key.namespace == args[1].lowercase()) {
                        p.location.chunk.removePDC(key)
                    }
                }
                return sendMessage(sender, "sure hope that wasn't a mistake!")
            }

            else -> return sendError(sender, "?")
        }
    }
}
