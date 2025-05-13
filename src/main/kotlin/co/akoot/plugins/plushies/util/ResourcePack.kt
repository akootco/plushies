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
    private var javaUrl: String? = null
    private var javaHash: String? = null

    val isPackEnabled: Boolean
        get() = conf.getBoolean("pack.enabled") == true

    val Player.sendPackLink: Boolean
        get() {
            Text(this) {
                Kolor.WARNING("click here to download")
                    .url(javaUrl ?: "").underlined()
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

    fun getJavaPack(): Boolean {
        val json = WebUtil.getJsonString("https://maltsburg.com/packs/java/notes") ?: return false
        val jsonObject = JsonParser.parseString(json).asJsonObject
        val notes = jsonObject.get("body").asString ?: return false

        javaUrl = jsonObject["assets"].asJsonArray[0].asJsonObject["browser_download_url"].asString
        javaHash = notes.substringAfter("`").take(40)
        return true
    }

    fun setPack(player: Player, force: Boolean = false): Boolean {
        if (player.isBedrock || !isPackEnabled) return false

        if (javaUrl != null && javaHash != null) {
            player.sendResourcePacks(
                ResourcePackRequest.resourcePackRequest()
                    .required(force)
                    .packs(
                        ResourcePackInfo.resourcePackInfo()
                            .uri(URI.create(javaUrl!!))
                            .hash(javaHash!!)
                    )
                    .build()
            )
        }
        return true
    }
}