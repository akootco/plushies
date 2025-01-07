package co.akoot.plugins.plushies.commands

import co.akoot.plugins.bluefox.api.FoxCommand
import co.akoot.plugins.bluefox.api.FoxConfig
import co.akoot.plugins.bluefox.api.FoxPlugin
import co.akoot.plugins.bluefox.util.Txt
import org.bukkit.command.CommandSender
import java.io.File

class LaysCommand(plugin: FoxPlugin) : FoxCommand(plugin, "lays") {

    private val configFile = File(plugin.dataFolder, "lays.conf")
    private val laysConfig = FoxConfig(configFile)
    private val chips = laysConfig.getStringList("chips").toMutableList()

    override fun onTabComplete(sender: CommandSender, args: Array<out String>): MutableList<String> {
        if (!hasPermission(sender, "edit")) return mutableListOf()

        if (args.size == 1) {
            return mutableListOf("add", "remove")
        } else if (args.size == 2 && (args[0] == ("remove") || args[0] == ("rem"))) {
            return chips
        }

        return mutableListOf()
    }

    override fun onCommand(sender: CommandSender, alias: String, args: Array<out String>): Boolean {

        if (args.isEmpty() || !hasPermission(sender, "edit")) return chip(sender)

        return when (args[0]) {

            "add" -> {
                if (args.size < 2) {
                    sendError(sender, "You must specify a chip to add!")
                    return false
                }

                addChip(args.copyOfRange(1, args.size).joinToString(" ")).send(sender).value
            }

            "remove" -> {
                if (args.size < 2) {
                    sendError(sender, "You must specify a chip to remove!")
                    return false
                }
                removeChip(args.copyOfRange(1, args.size).joinToString(" ")).send(sender).value
            }

            else -> {
                return chip(sender)
            }
        }
    }

    private fun chip(sender: CommandSender): Boolean {
        val p = playerCheck(sender) ?: return false

        if (chips.isEmpty()) return sendError(p, "No chips found!")

        val randomChip = chips.random()
        // set the player on fire if they roll a spicy chip
        if (arrayOf("Flamin", "Chile").any { it in randomChip }) p.fireTicks = 20

        p.chat(randomChip)
        return true
    }

    private fun addChip(chip: String): Result<Boolean> {
        return if (chips.contains(chip)) { // fail if the chip already exists
            Result.fail(
                (Txt("")
                        + Txt(chip).color("accent")
                        + Txt(" already exists!")).color("error_text").c
            )
        } else {
            chips.add(chip)
            laysConfig.set("chips", chips)
            Result.success(
                (Txt("")
                        + Txt(chip).color("accent")
                        + Txt(" has been added!")).color("text").c
            )
        }
    }

    private fun removeChip(chip: String): Result<Boolean> {
        return if (!chips.remove(chip)) { // fail if the chip does not exist
            Result.fail(
                (Txt("")
                        + Txt(chip).color("accent")
                        + Txt(" not found!")).color("error_text").c
            )
        } else {
            laysConfig.set("chips", chips)
            Result.success(
                (Txt("")
                        + Txt(chip).color("accent")
                        + Txt(" was removed!")).color("text").c
            )
        }
    }
}

