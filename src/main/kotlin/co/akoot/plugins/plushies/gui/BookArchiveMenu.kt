package co.akoot.plugins.plushies.gui

import co.akoot.plugins.bluefox.util.ColorUtil.randomColor
import co.akoot.plugins.bluefox.util.Text
import co.akoot.plugins.plushies.api.ChestMenu
import co.akoot.plugins.plushies.util.BookArchiver.loadBook
import co.akoot.plugins.plushies.util.Util.pl
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.BookMeta
import java.io.File

class BookArchiveMenu() : ChestMenu() {

    override val title =
        Text("Book Archive")
            .color(randomColor(brightness = 0.6f))
            .component

    override val items: List<ItemStack>
        get() {
            val files = File(pl.dataFolder, "books").listFiles() ?: return emptyList()

            return files.mapNotNull { file ->
                loadBook(file.nameWithoutExtension)
            }
        }

    override fun clickItem(player: Player, item: ItemStack) {
        if (item.type != Material.WRITTEN_BOOK) return

        player.openBook(item.itemMeta as BookMeta)
    }
}