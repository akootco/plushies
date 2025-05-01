package co.akoot.plugins.plushies.util

import co.akoot.plugins.bluefox.api.Kolor
import co.akoot.plugins.bluefox.extensions.isBedrock
import co.akoot.plugins.bluefox.util.Text
import co.akoot.plugins.bluefox.util.WebUtil
import co.akoot.plugins.plushies.Plushies.Companion.conf
import com.google.gson.JsonParser
import net.kyori.adventure.resource.ResourcePackInfo
import net.kyori.adventure.resource.ResourcePackRequest
import org.bukkit.entity.*
import java.net.URI

object ResourcePack {

    val packUrl: (String) -> String = { path -> "https://maltsburg.com/packs/$path" }

    val isPackEnabled: Boolean
        get() = conf.getBoolean("pack.enabled") == true

    val Player.sendPackLink: Boolean
        get() {
            Text(this) {
                Kolor.WARNING("click to download")
                    .url(packUrl("java")).underlined()
            }
            return true
        }

    val Player.sendPackMsg: Boolean
        get() {
            Text(this) {
                Kolor.ERROR("Resource pack was denied :(\n") +
                        Kolor.WARNING("click to enable").execute("/rp !")
            }
            return true
        }

    fun setPack(player: Player, force: Boolean = false): Boolean {

        if (player.isBedrock || !isPackEnabled) return false
        // get release notes
        val json = WebUtil.getJsonString(packUrl("java/notes")) ?: return false
        // extract hash from notes
        val body = JsonParser.parseString(json).asJsonObject.get("body")?.asString ?: return false

        player.sendResourcePacks(
            ResourcePackRequest.resourcePackRequest()
                .required(force)
                .packs(
                    ResourcePackInfo.resourcePackInfo()
                        .uri(URI(packUrl("java")))
                        .hash(body.substringAfter("`").take(40))
                )
                .build()
        )
        return true
    }
}