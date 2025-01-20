package co.akoot.plugins.plushies.listeners

import co.akoot.plugins.bluefox.util.Txt
import co.akoot.plugins.plushies.Plushies.Configs.conf
import io.papermc.paper.event.player.AsyncChatEvent

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitRunnable
import java.util.regex.Pattern

class PlayerEvents(private val plugin: Plugin) : Listener {

    private val msgConf = conf.getStringList("kickMsg").toMutableList()

    @EventHandler
    fun isHacking(event: AsyncChatEvent) {
        val p = event.player
        val msg = event.message().toString()

        if (!Pattern.compile("\\b(i\\s?('m|am)|im?)\\s?'?hacking\\b", Pattern.CASE_INSENSITIVE).matcher(msg).find()) return

        object : BukkitRunnable() {
            override fun run() {
                p.kick(Txt(msgConf.random()).c)
            }
        }.runTask(plugin)
    }
}