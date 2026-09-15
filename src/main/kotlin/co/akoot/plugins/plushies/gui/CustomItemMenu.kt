package co.akoot.plugins.plushies.gui

import co.akoot.plugins.plushies.api.ChestMenu
import co.akoot.plugins.plushies.util.Items.customItems
import co.akoot.plugins.plushies.util.Items.isCustomItem
import net.kyori.adventure.text.Component

class CustomItemMenu() : ChestMenu() {
    override val title = Component.text("Custom Items")
    override val items = customItems.values.filter { it.isCustomItem }
}