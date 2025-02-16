package co.akoot.plugins.plushies.commands

import co.akoot.plugins.bluefox.api.FoxCommand
import co.akoot.plugins.bluefox.api.FoxPlugin
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
                addResponse(args.copyOfRange(1, args.size).joinToString(" ")).send(sender).value
            }

            "remove", "rem" -> {
                if (args.size < 2) {
                    return sendError(sender, "You must specify a response to remove!")
                }
                removeResponse(args.copyOfRange(1, args.size).joinToString(" ")).send(sender).value
            }

            else -> {
                return response(sender)
            }
        }
    }

    private fun response(sender: CommandSender): Boolean {
        if (responses.isEmpty()) return sendError(sender, "No responses found!")
        Bukkit.getServer().sendMessage(
            (Text() // putting a hover on the start shows it for the whole message?
                    + Text("[AI]").color(MONTH_COLOR).hover("Ä_v1.0-alpha") // need to put it here I guess
                    + Text(" Akoot.AI ").color("player")
                    + Text("» " + responses.random()).color("text")).component
        )
        return true
    }

    private fun removeResponse(response: String): Result<Boolean> {
        return if (!responses.remove(response)) { // does it exist?
            Result.fail(
                (Text()
                        + Text(response).color("accent")
                        + Text(" not found!")).color("error_text").component
            )
        } else {
            aiConf.set("responses", responses) // not anymore!
            Result.success(
                (Text()
                        + Text(response).color("accent")
                        + Text(" was removed!")).color("text").component
            )
        }
    }

    private fun addResponse(response: String): Result<Boolean> {
        return if (responses.contains(response)) {
            // easily bypassed but oh well, that is what permissions are for.
            Result.fail(
                (Text()
                        + Text(response).color("accent")
                        + Text(" already exists!")).color("error_text").component
            )
        } else {
            responses.add(response)
            aiConf.set("responses", responses)
            Result.success(
                (Text()
                        + Text(response).color("accent")
                        + Text(" has been added!")).color("text").component
            )
        }
    }
}
