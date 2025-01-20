package co.akoot.plugins.plushies.util

import co.akoot.plugins.bluefox.util.Txt
import co.akoot.plugins.plushies.Plushies.Configs.conf
import net.kyori.adventure.resource.ResourcePackInfo
import net.kyori.adventure.resource.ResourcePackRequest
import net.kyori.adventure.text.logger.slf4j.ComponentLogger.logger
import org.bukkit.entity.*
import java.math.BigInteger
import java.net.URI

object ResourcePack {

    fun setPack(player: Player, force: Boolean = false): Boolean {

        if (player.name.startsWith(".")) return false

        if (conf.getBoolean("pack.enabled") == false) {
            player.sendMessage(Txt("Resource pack is disabled.", "error_accent").c)
            return false
        }

        val hash = conf.getString("pack.sha1")

        if (hash == null || hash.length != 40) {
            player.sendMessage(Txt("Resource pack hash is invalid.", "error_accent").c)
            logger().warn("Resource pack hash is invalid.")
            return false
        }

        val urlPath = conf.getString("pack.file")

        if (player.protocolVersion < 769) { // untested but i will just assume it works
            player.sendMessage(Txt("Support for versions below 1.21.4 will end soon.\nPlease update your client!", "error_accent").c)
            // please update soon :pwease:, i do not want to have to edit two files anymore
            // if server is updated beyond 1.21.4, this will be removed and the player will just have to deal with it.
        }

        player.sendResourcePacks(ResourcePackRequest.resourcePackRequest()
            .required(force)
            .packs(ResourcePackInfo.resourcePackInfo()
                    .uri(URI.create("https://maltsburg.com/packs/$urlPath.zip"))
                    .hash(BigInteger(hash, 16).toByteArray().toString())
            )
            .build())

        return true
    }
}