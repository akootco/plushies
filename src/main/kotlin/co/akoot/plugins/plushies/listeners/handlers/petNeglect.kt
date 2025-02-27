package co.akoot.plugins.plushies.listeners.handlers

import co.akoot.plugins.bluefox.util.Text
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.entity.Tameable
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.inventory.ItemStack
import java.util.regex.Pattern

fun petNeglect(player: Player, pItem: ItemStack, entity: Tameable, event: PlayerInteractEntityEvent) {
    if (pItem.type != Material.BLAZE_ROD || entity.isTamed) return

    val meta = pItem.itemMeta ?: return
    val displayName = meta.displayName()?.let { PlainTextComponentSerializer.plainText().serialize(it) } ?: return

    if (!Pattern.compile("\\s*be\\s*free\\s*", Pattern.CASE_INSENSITIVE).matcher(displayName).matches()) return

    if (entity.owner?.uniqueId != player.uniqueId) {
        player.sendMessage(
            (Text("${entity.name} belongs to ") +
                    Text(entity.owner?.name.toString(), "player")
                        .execute("/profile ${entity.owner?.name}")).component
        )
        event.isCancelled = true
        return
    }

    entity.isTamed = false
    event.isCancelled = true
    player.sendMessage((Text(entity.name, "accent") + Text(" is no longer tamed!", "text")).component)
    return
}