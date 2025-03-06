package co.akoot.plugins.plushies.commands

import co.akoot.plugins.bluefox.api.FoxCommand
import co.akoot.plugins.bluefox.api.FoxPlugin
import co.akoot.plugins.bluefox.api.Kolor
import co.akoot.plugins.bluefox.extensions.invoke
import co.akoot.plugins.bluefox.util.ColorUtil.MONTH_COLOR
import co.akoot.plugins.bluefox.util.Text
import co.akoot.plugins.plushies.Plushies.Companion.aiConf
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender

class AICommand(plugin: FoxPlugin) : FoxCommand(plugin, "ai", aliases = arrayOf("akootai")) {

    private val responses = aiConf.getStringList("responses").toMutableList()

    override fun onTabComplete(sender: CommandSender, alias: String, args: Array<out String>): MutableList<String> {
        if (!hasPermission(sender, "edit")) return mutableListOf()

        if (args.size == 1) {
            return mutableListOf("add", "remove")
        } else if (args[0] in setOf("remove", "rem")) {
            return responses
        }

        return mutableListOf()
    }

    override fun onCommand(sender: CommandSender, alias: String, args: Array<out String>): Boolean {
        // TODO: should probably add a cooldown
        if (args.isEmpty() || !hasPermission(sender, "edit")) return response(sender)

        return when (args[0]) {

            "add" -> {
                if (args.size < 2) {
                    return sendError(sender, "You must specify a response to add!")
                }
                addResponse(args.copyOfRange(1, args.size).joinToString(" ")).getAndSend(sender)
            }

            "remove", "rem" -> {
                if (args.size < 2) {
                    return sendError(sender, "You must specify a response to remove!")
                }
                removeResponse(args.copyOfRange(1, args.size).joinToString(" ")).getAndSend(sender)
            }

            else -> {
                return response(sender)
            }
        }
    }

    private fun response(sender: CommandSender): Boolean {
        if (responses.isEmpty()) return sendError(sender, "No responses found!")
        val message = Kolor.MONTH("[AI]").hover("Ä_v1.0-alpha") +
                    Kolor.PLAYER(" Akoot.AI ") +
                    Kolor.TEXT("» " + responses.random())
        message.broadcast()
        return true
    }

    private fun removeResponse(response: String): Result<Boolean> {
        return if (!responses.remove(response)) { // does it exist?
            Result.fail(Kolor.ACCENT(response) + Kolor.ERROR.text(" not found!"))
        } else {
            aiConf.set("responses", responses) // not anymore!
            Result.success(Kolor.ACCENT(response) + Kolor.TEXT(" was removed!"))
        }
    }

    private fun addResponse(response: String): Result<Boolean> {
        return if (responses.contains(response)) {
            // easily bypassed but oh well, that is what permissions are for.
            Result.fail(Kolor.ERROR.accent(response) + Kolor.ERROR(" already exists!"))
        } else {
            responses.add(response)
            aiConf.set("responses", responses)
            Result.success(Kolor.ERROR.accent(response) + Kolor.ERROR(" has been added!"))
        }
    }
}
