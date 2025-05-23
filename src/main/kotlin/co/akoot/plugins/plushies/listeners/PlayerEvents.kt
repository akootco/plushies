package co.akoot.plugins.plushies.listeners

import co.akoot.plugins.bluefox.api.FoxPlugin
import co.akoot.plugins.bluefox.extensions.getPDC
import co.akoot.plugins.bluefox.extensions.isBedrock
import co.akoot.plugins.bluefox.util.Text
import co.akoot.plugins.plushies.Plushies.Companion.conf
import co.akoot.plugins.plushies.Plushies.Companion.key
import co.akoot.plugins.plushies.listeners.tasks.Throwable.Companion.axeKey
import co.akoot.plugins.plushies.listeners.tasks.Throwable.Companion.spawnThrowable
import co.akoot.plugins.plushies.util.ResourcePack.isPackEnabled
import co.akoot.plugins.plushies.util.ResourcePack.sendPackLink
import co.akoot.plugins.plushies.util.ResourcePack.sendPackMsg
import co.akoot.plugins.plushies.util.ResourcePack.setPack
import co.akoot.plugins.plushies.util.Util.setAttributes
import io.papermc.paper.event.player.AsyncChatEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerItemConsumeEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerResourcePackStatusEvent
import org.bukkit.scheduler.BukkitRunnable
import java.util.UUID
import java.util.regex.Pattern

class PlayerEvents(private val plugin: FoxPlugin) : Listener {

    private val msgConf = conf.getStringList("kickMsg").toMutableList()
    private val messageSent: MutableList<UUID> = mutableListOf()

    @EventHandler
    fun itemConsume(event: PlayerItemConsumeEvent) {
        if (event.item.itemMeta.getPDC<String>(key("attributes")) != null)
            setAttributes(event.item, event.player)
    }

    @EventHandler
    fun isHacking(event: AsyncChatEvent) {
        val p = event.player
        val msg = event.message().toString()

        if (!Pattern.compile("\\b(i\\s?('m|am)|im?)\\s?'?hacking\\b", Pattern.CASE_INSENSITIVE).matcher(msg).find()) return

        object : BukkitRunnable() {
            override fun run() {
                p.kick(Text(msgConf.random()).component)
            }
        }.runTask(plugin)
    }

    @EventHandler
    fun PlayerJoinEvent.onJoin() {
        if (!isPackEnabled) return
        setPack(player)
    }

    @EventHandler
    fun PlayerResourcePackStatusEvent.onPackDeny() {
        if (player.isBedrock || !isPackEnabled) return

        if (status == PlayerResourcePackStatusEvent.Status.DECLINED && !messageSent.contains(player.uniqueId)) {
            messageSent.add(player.uniqueId)
            player.apply { sendPackMsg; sendPackLink }
        }
    }

    @EventHandler
    fun playerInteract(event: PlayerInteractEvent) {
        val player = event.player

        when (event.action) {
            Action.RIGHT_CLICK_AIR -> {
                if (player.inventory.itemInMainHand.persistentDataContainer.has(axeKey)) {
                    spawnThrowable(player, plugin)
                }
            }

            else -> return
        }
    }
}