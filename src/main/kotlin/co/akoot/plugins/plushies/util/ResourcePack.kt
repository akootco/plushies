package co.akoot.plugins.plushies.util

import co.akoot.plugins.bluefox.api.Kolor
import co.akoot.plugins.bluefox.extensions.isBedrock
import co.akoot.plugins.bluefox.util.Text
import co.akoot.plugins.bluefox.util.WebUtil
import co.akoot.plugins.plushies.Plushies.Companion.conf
import com.google.gson.JsonParser
import net.kyori.adventure.resource.ResourcePackInfo
import net.kyori.adventure.resource.ResourcePackRequest
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.entity.*
import java.net.URI
import java.util.*

object ResourcePack {

    val packDeniers: MutableList<UUID> = mutableListOf()

    var isPackNew: Boolean = false

    val isPackEnabled: Boolean
        get() = conf.getBoolean("pack.enabled") == true

    val javaPackLink: String
        get() = conf.getString("pack.link") ?: " "

    val javaPackHash: String
        get() = conf.getString("pack.hash") ?: " "

    val Player.sendPackMsg: Boolean
        get() {
            Text(this) {
                Text() +
                Kolor.MONTH("Download").url(javaPackLink).hover("Link to Github") +
                        Kolor.QUOTE(" | ").decorate(TextDecoration.BOLD) +
                        Kolor.MONTH("Enable").execute("/rp !")
                            .hover("Sadly this will disconnect you from the server if denied.")
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
        } else return false

        // clear deniers so they get the message again
        packDeniers.clear()
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