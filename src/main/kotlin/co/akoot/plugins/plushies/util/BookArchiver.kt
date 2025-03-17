package co.akoot.plugins.plushies.util

import co.akoot.plugins.bluefox.api.FoxPlugin
import co.akoot.plugins.bluefox.api.Kolor
import co.akoot.plugins.bluefox.util.Text
import co.akoot.plugins.plushies.util.Util.loadYamlConfig
import co.akoot.plugins.plushies.util.builders.ItemBuilder
import io.papermc.paper.datacomponent.DataComponentTypes
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.BookMeta
import java.io.File

class BookArchiver(private val plugin: FoxPlugin) {

    fun saveBook(player: Player, book: ItemStack) {
        val meta = book.itemMeta as? BookMeta ?: run {
            Text(player) { Kolor.ERROR("This is not a book!") }
            return
        }

        // clean the name
        val fileName =
            "${meta.title}.yml"
                .lowercase()
                .replace(" ", "_")
                .replace(Regex("[^a-zA-Z0-9_.]"), "")

        /*
         * Check if the book is already archived
         * clone the item and remove custom name and custom model data
         */
        if (loadYamlConfig(plugin, "books/$fileName")
                .getItemStack("book")?.isSimilar(
                    ItemBuilder.builder(book.clone())
                        .unsetData(DataComponentTypes.CUSTOM_NAME)
                        .unsetData(DataComponentTypes.CUSTOM_MODEL_DATA)
                        .build()) == true) {
            Text(player) { Kolor.ERROR("This book is already archived!") }
            return
        }

        val bookConfig = loadYamlConfig(plugin, "books/$fileName")

        try {
            // save the book
            bookConfig.set("book", book)
            bookConfig.save(File(plugin.dataFolder, "books/$fileName"))
            Text(player) { Kolor.TEXT("Saved book: ${meta.title}") }
        } catch (e: Exception) {
            Text(player) { Kolor.ERROR("Failed to save book: ${meta.title}") }
        }
    }

    fun loadBook(bookTitle: String): ItemStack? {
        return loadYamlConfig(plugin, "books/${bookTitle}.yml")
            .getItemStack("book")
    }
}