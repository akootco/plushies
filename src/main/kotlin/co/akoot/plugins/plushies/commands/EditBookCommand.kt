package co.akoot.plugins.plushies.commands

import co.akoot.plugins.bluefox.api.FoxCommand
import co.akoot.plugins.bluefox.api.FoxPlugin
import co.akoot.plugins.bluefox.api.Kolor
import co.akoot.plugins.bluefox.util.Text
import co.akoot.plugins.plushies.util.builders.ItemBuilder
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.BookMeta

class EditBookCommand(plugin: FoxPlugin) :
    FoxCommand(plugin, "editbook", description = "Edit written books", aliases = arrayOf("edit", "eb")) {

    override fun onTabComplete(sender: CommandSender, alias: String, args: Array<out String>): MutableList<String> {
        return if (args.size == 1) mutableListOf("lock") else mutableListOf()
    }

    override fun onCommand(sender: CommandSender, alias: String, args: Array<out String>): Boolean {
        val p = playerCheck(sender) ?: return false

        val item = p.inventory.itemInMainHand

        if (p.inventory.itemInMainHand.type != Material.WRITTEN_BOOK) {
            return sendError(p, "You need to be holding a written book!")
        }

        val bookMeta = item.itemMeta as BookMeta

        // send message and return if sender is not the author
        if (bookMeta.author != p.name) {
            Text(p) {
                Kolor.TEXT("Nice try, pal! This book belongs to ") +
                        Kolor.PLAYER(bookMeta.author ?: "Unknown Author")
            }
            return false
        }

        when (args.getOrNull(0)) {
            "lock" -> {
                // book will not be able to be copied
                bookMeta.generation = BookMeta.Generation.TATTERED
                item.itemMeta = bookMeta
                return true
            }

            else -> {
                p.inventory.setItemInMainHand(
                    ItemBuilder.builder(ItemStack(Material.WRITABLE_BOOK))
                        .copyToBook(item)
                        .build()
                )
                return true
            }
        }
    }
}