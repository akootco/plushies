package co.akoot.plugins.plushies.gui

import co.akoot.plugins.bluefox.util.text
import co.akoot.plugins.plushies.api.ChestMenu
import co.akoot.plugins.plushies.util.Items.customItems
import co.akoot.plugins.plushies.util.Items.isCustomItem

class CustomItemMenu(page: Int = 1) : ChestMenu(page) {

    override val title = text("Custom Items")

    override val items = customItems.values.filter { it.isCustomItem }

    override fun nextPage() = CustomItemMenu(page + 1)

    override fun prevPage() = CustomItemMenu(page - 1)
}