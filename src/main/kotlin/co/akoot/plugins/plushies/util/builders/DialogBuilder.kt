package co.akoot.plugins.plushies.util.builders

import co.akoot.plugins.bluefox.extensions.withDisplayName
import co.akoot.plugins.bluefox.util.error
import co.akoot.plugins.bluefox.util.parse
import co.akoot.plugins.bluefox.util.text
import io.papermc.paper.dialog.Dialog
import io.papermc.paper.dialog.DialogResponseView
import io.papermc.paper.registry.data.dialog.ActionButton
import io.papermc.paper.registry.data.dialog.DialogBase
import io.papermc.paper.registry.data.dialog.action.DialogAction
import io.papermc.paper.registry.data.dialog.body.DialogBody
import io.papermc.paper.registry.data.dialog.input.DialogInput
import io.papermc.paper.registry.data.dialog.input.SingleOptionDialogInput
import io.papermc.paper.registry.data.dialog.input.TextDialogInput
import io.papermc.paper.registry.data.dialog.type.DialogType
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickCallback
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

fun dialog(block: DialogBuilder.() -> Unit): Dialog {
    return DialogBuilder()
        .apply(block)
        .build()
}

class DialogBuilder {
    private var title: Component = Component.text("Custom Menu!")
    private val buttons: MutableList<ActionButton> = mutableListOf()
    private val bodies: MutableList<DialogBody> = mutableListOf()
    private val inputs: MutableList<DialogInput> = mutableListOf()

    fun title(title: Component): DialogBuilder {
        this.title = title
        return this
    }

    fun message(component: Component, width: Int = 600): DialogBuilder {
        bodies.add(DialogBody.plainMessage(component, width))
        return this
    }

    fun message(text: String, width: Int = 600): DialogBuilder {
        message(text(text), width)
        return this
    }

    fun slider(label: String, range: ClosedFloatingPointRange<Float>): DialogBuilder {
        inputs.add(
            DialogInput.numberRange(label.lowercase(), text(label), range.start, range.endInclusive)
                .initial(1f)
                .step(1f)
                .width(200)
                .build()
        )
        return this
    }

    // why ts not a drop down??
    fun select(label: String, options: List<String>, initial: String? = null): DialogBuilder {
        inputs.add(
            DialogInput.singleOption(
                label.lowercase(),
                text(label),
                options.map { option ->
                    SingleOptionDialogInput.OptionEntry.create(
                        option,
                        option.parse(),
                        option == initial
                    )
                }
            ).build()
        )
        return this
    }

    // idk what ts means by value in template, oh well
    fun toggle(label: String, initial: Boolean, onTrue: String = "Enabled", onFalse: String = "Disabled"): DialogBuilder {
        inputs.add(DialogInput.bool(label.lowercase(), text(label), initial, onTrue, onFalse))
        return this
    }

    fun textInput(label: String, initial: String?, maxLines: Int = 1, maxLength: Int = 256): DialogBuilder {
        inputs.add(
            DialogInput.text(
                label.lowercase(),
                300,
                text(label),
                true,
                initial ?: "",
                maxLength,
                TextDialogInput.MultilineOptions.create(maxLines, null)
            )
        )
        return this
    }

    fun icon(item: ItemStack?, description: Component? = null): DialogBuilder {
        val desc = description?.let { DialogBody.plainMessage(it) }
        val errorItem = ItemStack(Material.BARRIER)
            .withDisplayName(error("oops, this item doesn't exist!"))
        bodies.add(DialogBody.item(item ?: errorItem, desc, true, true, 16, 16))
        return this
    }

    // apparently this is the method to avoid using the CustomClickEvent
    fun callback(
        label: Component,
        width: Int = 100,
        action: (Player, DialogResponseView) -> Unit
    ): DialogBuilder {
        buttons.add(
            ActionButton.create(
                label,
                Component.empty(),
                width,
                DialogAction.customClick(
                    { view, audience ->
                        if (audience is Player) {
                            action(audience, view)
                        }
                    },
                    ClickCallback.Options.builder().build()
                )
            )
        )
        return this
    }

    fun build(): Dialog {
        return Dialog.create { builder ->
            builder.empty()
                .base(DialogBase.create(title,
                    null,
                    false,
                    false,
                    DialogBase.DialogAfterAction.CLOSE,
                    bodies,
                    inputs
                )).apply {
                    if (buttons.isNotEmpty()) {
                        type(DialogType.multiAction(buttons).columns(3).build()) // column needs to be configrable later
                    } else {
                        type(DialogType.notice())
                    }
                }
        }
    }
}