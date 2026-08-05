package co.akoot.plugins.plushies.commands

import co.akoot.plugins.bluefox.api.CatCommand
import co.akoot.plugins.bluefox.extensions.withDisplayName
import co.akoot.plugins.bluefox.util.*
import co.akoot.plugins.bluefox.util.Text.Companion.asString
import co.akoot.plugins.plushies.Plushies
import co.akoot.plugins.plushies.util.Util.autoMend
import co.akoot.plugins.bluefox.api.dialog
import io.papermc.paper.datacomponent.DataComponentTypes
import io.papermc.paper.dialog.Dialog
import org.bukkit.entity.Player

class DialogTestCommand(plugin: Plushies) : CatCommand(plugin, "dlt") {
    init {
        noargs {
            val player = getPlayerSender(it) ?: return@noargs false
            player.showDialog(settingsMenu(player))
            true
        }
        then {
            subcommand("item") {
                val player = getPlayerSender(it) ?: return@subcommand false
                player.showDialog(itemSettingsMenu(player))
                true
            }
        }
    }

    private fun settingsMenu(player: Player): Dialog {
        return dialog {
            title(text("Hello ${player.name}!"))

            message("check out these settings!")

            // real toggle is a dumb checkbox/ maybe use button(callback) instead
            button(
                (Color.Text + "AutoMend: ").append(
                    player.autoMend.get(Color.May + "Enabled", Color.Error + "Disabled")
                )
            ) { p, _ ->
                // toggle the setting and reopen the menu to update button text
                p.autoMend = !p.autoMend
                p.showDialog(settingsMenu(p))
            }

        }
    }

    private fun itemSettingsMenu(player: Player): Dialog {
        val item = player.inventory.itemInMainHand
        return dialog {
            title(text("Hello ${player.name}!"))

            message("check out these settings!")

            // default max char 256
            textInput("name","Name".text, item.effectiveName().asString())

            toggle("glint","Glint".text, item.getData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE) == true)

            button(text("Save Changes")) { p, view ->
                // ts busted lmao you get the idea
                val itemName = view.getText("name") ?: return@button
                val glint = view.getBoolean("glint") ?: return@button

                item.apply {
                    withDisplayName(itemName.parse())
                    setData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, glint)
                }
            }
        }
    }
}