package co.akoot.plugins.plushies.commands

import co.akoot.plugins.bluefox.api.FoxCommand
import co.akoot.plugins.bluefox.api.FoxConfig
import co.akoot.plugins.bluefox.api.FoxPlugin
import co.akoot.plugins.bluefox.util.ColorUtil.MONTH_COLOR
import co.akoot.plugins.bluefox.util.Txt
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import java.io.File

class AICommand(plugin: FoxPlugin) : FoxCommand(plugin, "ai", aliases = arrayOf("akootai")) {

    private val configFile = File(plugin.dataFolder, "ai.conf")
    private val laysConfig = FoxConfig(configFile)
    private val responses = laysConfig.getStringList("responses").toMutableList()

    override fun onTabComplete(sender: CommandSender, args: Array<out String>): MutableList<String> {
        if (!hasPermission(sender, "edit")) return mutableListOf()

        if (args.size == 1) {
            return mutableListOf("add", "remove")
        } else if (args.size == 2 && (args[0] == ("remove") || args[0] == ("rem"))) {
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
                    sendError(sender, "You must specify a response to add!")
                    return false
                }
                addResponse(args.copyOfRange(1, args.size).joinToString(" ")).send(sender).value
            }

            "remove", "rem" -> {
                if (args.size < 2) {
                    sendError(sender, "You must specify a response to remove!")
                    return false
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
            (Txt("") // putting a hover on the start shows it for the whole message?
                    + Txt("[AI]").color(MONTH_COLOR).hover("Ä_v1.0-alpha") // need to put it here I guess
                    + Txt(" Akoot.AI ").color("player")
                    + Txt("» " + responses.random()).color("text")).c
        )
        return true
    }

    private fun removeResponse(response: String): Result<Boolean> {
        return if (!responses.remove(response)) { // does it exist?
            Result.fail(
                (Txt("")
                        + Txt(response).color("accent")
                        + Txt(" not found!")).color("error_text").c
            )
        } else {
            laysConfig.set("responses", responses) // not anymore!
            Result.success(
                (Txt("")
                        + Txt(response).color("accent")
                        + Txt(" was removed!")).color("text").c
            )
        }
    }

    private fun addResponse(response: String): Result<Boolean> {
        return if (responses.contains(response)) {
            // easily bypassed but oh well, that is what permissions are for.
            Result.fail(
                (Txt("")
                        + Txt(response).color("accent")
                        + Txt(" already exists!")).color("error_text").c
            )
        } else {
            responses.add(response)
            laysConfig.set("responses", responses)
            Result.success(
                (Txt("")
                        + Txt(response).color("accent")
                        + Txt(" has been added!")).color("text").c
            )
        }
    }
}
