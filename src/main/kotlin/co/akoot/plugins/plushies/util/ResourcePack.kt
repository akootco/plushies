package co.akoot.plugins.plushies.util

import co.akoot.plugins.bluefox.api.FoxCommand
import co.akoot.plugins.bluefox.api.Kolor
import co.akoot.plugins.bluefox.extensions.invoke
import co.akoot.plugins.bluefox.util.Text
import co.akoot.plugins.plushies.Plushies.Companion.conf
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
            return FoxCommand.Result.fail("Resource pack is disabled.").getAndSend(player)
        }

        val hash = conf.getString("pack.sha1")

        if (hash == null || hash.length != 40) {
            logger().warn("Resource pack hash is invalid.")
            return FoxCommand.Result.fail("Resource pack hash is invalid.").getAndSend(player)
        }

        val urlPath = conf.getString("pack.file")

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