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

    var isPackNew: Boolean = false

    val isPackEnabled: Boolean
        get() = conf.getBoolean("pack.enabled") == true

    val javaPackLink: String
        get() = conf.getString("pack.link") ?: " "

    val javaPackHash: String
        get() = conf.getString("pack.hash") ?: " "


    val Player.sendPackLink: Boolean
        get() {
            Text(this) {
                Kolor.WARNING("Click here to download").url(javaPackLink).apply {
                    if (isPackNew) {
                        plus(Kolor.WARNING("\nYour pack is out of date!"))
                    }
                }
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
        val jsonString = WebUtil.getJsonString("https://maltsburg.com/packs/java/notes") ?: return false
        val jsonObject = JsonParser.parseString(jsonString).asJsonObject

        val url = jsonObject["assets"].asJsonArray[0].asJsonObject["browser_download_url"].asString
        val hash = jsonObject.get("body").asString.substringAfter("`").take(40)

        val currentUrl = conf.getString("pack.link")
        val currentHash = conf.getString("pack.hash")

        // Only update if the link or hash are truly different.
        if (url != currentUrl || hash != currentHash) {
            conf.set("pack.link", url)
            conf.set("pack.hash", hash)
            isPackNew = true
        }

        return true
    }

    fun setPack(player: Player, force: Boolean = false): Boolean {
        if (player.isBedrock || !isPackEnabled) return false

        player.sendResourcePacks(
            ResourcePackRequest.resourcePackRequest()
                .required(force)
                .packs(
                    ResourcePackInfo.resourcePackInfo()
                        .uri(URI.create(javaPackLink))
                        .hash(javaPackHash)
                )
                .build()
        )
        return true
    }
}