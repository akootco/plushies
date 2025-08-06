package co.akoot.plugins.plushies.util.builders

import co.akoot.plugins.bluefox.api.Kolor
import co.akoot.plugins.bluefox.util.Text
import io.papermc.paper.dialog.Dialog
import io.papermc.paper.registry.data.dialog.ActionButton
import io.papermc.paper.registry.data.dialog.DialogBase
import io.papermc.paper.registry.data.dialog.action.DialogAction
import io.papermc.paper.registry.data.dialog.body.DialogBody
import io.papermc.paper.registry.data.dialog.type.DialogType
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class DialogBuilder {

    private var title: Component = Kolor.MONTH("Custom Dialog Screen!").component
    private val buttons: MutableList<ActionButton> = mutableListOf()
    private var bodies: MutableList<DialogBody> = mutableListOf()

    fun title(title: Text): DialogBuilder {
        this.title = title.component
        return this
    }

    fun text(text: Text, width: Int = 600): DialogBuilder {
        bodies.add(DialogBody.plainMessage(text.component, width))
        return this
    }

    fun icon(item: ItemStack?, description: Text? = null): DialogBuilder {
        val desc = description?.let { DialogBody.plainMessage(it.component) }
        val errorItem = ItemBuilder.builder(ItemStack(Material.BARRIER)).itemName(Kolor.ERROR("oops, this item doesn't exist!").component).build()
        bodies.add(DialogBody.item(item?:errorItem, desc, true, true, 16, 16))
        return this
    }

    fun runcmd(text: Text, hover: Text = Text(), command: String, width: Int = 100): DialogBuilder {
        buttons.add(
            ActionButton.create(
                text.component,
                hover.component,
                width,
                DialogAction.staticAction(ClickEvent.runCommand(command))
            )
        )
        return this
    }

    fun btn(text: Text, hover: Text = Text(), clickEvent: ClickEvent, width: Int = 100): DialogBuilder {
        buttons.add(
            ActionButton.create(
                text.component,
                hover.component,
                width,
                DialogAction.staticAction(clickEvent)
            )
        )
        return this
    }

    fun build(): Dialog {
        return Dialog.create { builder ->
            builder.empty()
                .base(DialogBase.create(title,
                    null,
                    true,
                    false,
                    DialogBase.DialogAfterAction.CLOSE,
                    bodies,
                    emptyList()))
                .apply {
                    if (buttons.isNotEmpty()) {
                        type(DialogType.multiAction(buttons).columns(3).build())
                    } else {
                        type(DialogType.notice())
                    }
                }
        }
    }
}