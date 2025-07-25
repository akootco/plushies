package co.akoot.plugins.plushies.listeners

import co.akoot.plugins.bluefox.api.FoxPlugin
import co.akoot.plugins.bluefox.api.Kolor
import co.akoot.plugins.bluefox.extensions.hasPDC
import co.akoot.plugins.bluefox.extensions.isBedrock
import co.akoot.plugins.bluefox.util.Text
import co.akoot.plugins.bluefox.util.runLater
import co.akoot.plugins.plushies.Plushies.Companion.conf
import co.akoot.plugins.plushies.Plushies.Companion.key
import co.akoot.plugins.plushies.listeners.handlers.placeItem
import co.akoot.plugins.plushies.listeners.tasks.Throwable.Companion.axeKey
import co.akoot.plugins.plushies.listeners.tasks.Throwable.Companion.spawnThrowable
import co.akoot.plugins.plushies.util.Items.isPlaceable
import co.akoot.plugins.plushies.util.Recipes.unlockRecipes
import co.akoot.plugins.plushies.util.ResourcePack.isPackEnabled
import co.akoot.plugins.plushies.util.ResourcePack.packDeniers
import co.akoot.plugins.plushies.util.ResourcePack.sendPackMsg
import co.akoot.plugins.plushies.util.ResourcePack.setPack
import co.akoot.plugins.plushies.util.Util.setAttributes
import io.papermc.paper.event.player.AsyncChatEvent
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerItemConsumeEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerResourcePackStatusEvent
import java.util.regex.Pattern

class PlayerEvents(private val plugin: FoxPlugin) : Listener {

    private val msgConf = conf.getStringList("kickMsg").toMutableList()

    @EventHandler
    fun itemConsume(event: PlayerItemConsumeEvent) {
        val meta = event.item.itemMeta ?: return

        when {
            meta.hasPDC(key("attributes")) ->
                setAttributes(event.item, event.player)

            meta.hasPDC(axeKey) -> {
                spawnThrowable(event, plugin)
                event.isCancelled = true
            }
        }
    }

    @EventHandler
    fun isHacking(event: AsyncChatEvent) {
        if (!Pattern.compile("\\b(i\\s?('m|am)|im?)\\s?'?hacking\\b", Pattern.CASE_INSENSITIVE).matcher(event.message().toString()).find()) return
        runLater(1) { event.player.kick(Text(msgConf.random()).component) }
    }

    @EventHandler
    fun PlayerJoinEvent.onJoin() {
        unlockRecipes(player)
        if (isPackEnabled) {
            setPack(player)
        }
    }

    @EventHandler
    fun PlayerResourcePackStatusEvent.onPackDeny() {
        if (player.isBedrock || !isPackEnabled) return

        if (status == PlayerResourcePackStatusEvent.Status.DECLINED && !packDeniers.contains(player.uniqueId)) {
            packDeniers.add(player.uniqueId)
            Text(player) { Kolor.WARNING("Resource pack was denied").hover("sad") }
            player.sendPackMsg
        }
    }

    @EventHandler
    fun playerInteract(event: PlayerInteractEvent) {
        val player = event.player
        val item = player.inventory.itemInMainHand

        when (event.action) {
//            Action.RIGHT_CLICK_AIR -> {
//                if (item.persistentDataContainer.has(axeKey)) {
//                    spawnThrowable(player, plugin)
//                }
//            }

            Action.RIGHT_CLICK_BLOCK -> {
                val block = event.clickedBlock ?: return
                val face = event.blockFace
                if (item.isPlaceable &&
                    block.isSolid && player.isSneaking &&
                    block.getRelative(face).type == Material.AIR
                ) {
                    placeItem(face, player)
                }
            }
            else -> return
        }
    }
}