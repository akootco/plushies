package co.akoot.plugins.plushies.listeners

import co.akoot.plugins.bluefox.util.Txt
import co.akoot.plugins.plushies.gui.BookMenu
import co.akoot.plugins.plushies.gui.Plush.home
import co.akoot.plugins.plushies.gui.Plush.nextPage
import co.akoot.plugins.plushies.gui.Plush.pMenu
import co.akoot.plugins.plushies.gui.Plush.plushMsg
import co.akoot.plugins.plushies.gui.Plush.prevPage
import co.akoot.plugins.plushies.gui.Plush.sMenu
import co.akoot.plugins.plushies.gui.PlushieMenu
import co.akoot.plugins.plushies.util.builders.ItemBuilder
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent

class GUI : Listener {
    @EventHandler
    fun onInvClick(event: InventoryClickEvent) {
        val menuItem = event.currentItem
        val p = event.whoClicked
        val pInv = p.inventory
        val holder = event.clickedInventory?.holder
        val pItem = pInv.itemInMainHand

        when (holder ?: return) {
            is PlushieMenu -> {
                when (menuItem) {
                    home -> p.openInventory(PlushieMenu().mainMenu())
                    pMenu -> p.openInventory(PlushieMenu().inventory)
                    sMenu -> p.openInventory(PlushieMenu(true).inventory)
                    nextPage -> p.openInventory((holder as PlushieMenu).nextPage().inventory)
                    prevPage -> p.openInventory((holder as PlushieMenu).prevPage().inventory)
                }

                if (menuItem?.type == Material.TOTEM_OF_UNDYING && menuItem != pMenu) { // is it a friend?

                    if (pItem.type != Material.TOTEM_OF_UNDYING) { // HEY! no hacking!
                        p.sendMessage(Txt("You must be holding a totem!", "error_accent").c)
                    } else {
                        when ((holder as PlushieMenu).isStatue()) {
                            true -> ItemBuilder.builder(pItem).copyOf(menuItem)
                                .customModelData(pItem.itemMeta.customModelData + 1).build() // statue

                            false -> ItemBuilder.builder(pItem).copyOf(menuItem).build() // normal
                        }

                        plushMsg(PlainTextComponentSerializer.plainText().serialize(menuItem.effectiveName())).c
                    }
                }
                event.isCancelled = true
            }

            is BookMenu -> {
                val book = Material.WRITTEN_BOOK

                when (menuItem) {
                    nextPage -> p.openInventory((holder as BookMenu).nextPage().inventory)
                    prevPage -> p.openInventory((holder as BookMenu).prevPage().inventory)
                }

                if (menuItem?.type == book) {
                    if (pItem.type != book) { // HEY! no hacking!
                        p.sendMessage(Txt("You must be holding a written book!", "error_accent").c)
                    } else {
                        pInv.setItemInMainHand(
                            ItemBuilder.builder(pItem)
                                .customModelData(menuItem.itemMeta.customModelData)
                                .build()
                        )
                    }
                }
                event.isCancelled = true
            }
        }
    }
//    @EventHandler
//    fun onInvClose(event: InventoryCloseEvent) {
//        val inv = event.inventory
//        val menu = inv.getHolder(false)
//        val p = event.player
//        when (menu) {
//            is PlushieMainMenu -> {
//                p.sendMessage(Txt("Test").color("error_accent").c)
//            }
//        }
//    }
}