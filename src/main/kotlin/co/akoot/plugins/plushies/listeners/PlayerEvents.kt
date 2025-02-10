package co.akoot.plugins.plushies.listeners

import co.akoot.plugins.bluefox.api.FoxPlugin
import co.akoot.plugins.bluefox.util.Text
import co.akoot.plugins.plushies.Plushies.Configs.conf
import co.akoot.plugins.plushies.listeners.tasks.Throwable.Companion.axeKey
import co.akoot.plugins.plushies.listeners.tasks.Throwable.Companion.spawnThrowable
import co.akoot.plugins.plushies.util.ResourcePack.setPack
import io.papermc.paper.event.player.AsyncChatEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerResourcePackStatusEvent
import org.bukkit.scheduler.BukkitRunnable
import java.util.UUID
import java.util.regex.Pattern

class PlayerEvents(private val plugin: FoxPlugin) : Listener {

    private val msgConf = conf.getStringList("kickMsg").toMutableList()
    private val messageSent: MutableList<UUID> = mutableListOf()

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
    fun onJoin(event: PlayerJoinEvent) {
        if (conf.getBoolean("pack.enabled") == false) return
        setPack(event.player)
    }

    @EventHandler
    fun onPackDeny(event: PlayerResourcePackStatusEvent) {
        if (conf.getBoolean("pack.enabled") == false) return

        val player = event.player

        if (player.name.startsWith(".")) return

        if (event.status == PlayerResourcePackStatusEvent.Status.DECLINED && !messageSent.contains(player.uniqueId)) {
            // pack deniers(haters) are in the same boat as rule book dumpers :angerysad:
            player.sendMessage((Text("Resource pack was denied.\n" , "error_accent")
                    + Text("Click here to enable it", "accent").execute("/rp !")).component)

            messageSent.add(player.uniqueId)
        }
    }

    @EventHandler
    fun playerInteract(event: PlayerInteractEvent) {
        val player = event.player
        if (event.action == Action.RIGHT_CLICK_AIR) {
            if (player.inventory.itemInMainHand.persistentDataContainer.has(axeKey)) {
                spawnThrowable(player, plugin)
            }
        }
    }
}