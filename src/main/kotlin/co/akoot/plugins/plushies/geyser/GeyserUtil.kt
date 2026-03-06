package co.akoot.plugins.plushies.geyser

import co.akoot.plugins.bluefox.util.WebUtil
import co.akoot.plugins.plushies.util.Util.pl
import org.bukkit.Tag
import org.bukkit.Material
import org.geysermc.geyser.api.GeyserApi
import org.geysermc.geyser.api.event.lifecycle.GeyserDefineCustomItemsEvent
import org.geysermc.geyser.api.item.custom.v2.CustomItemBedrockOptions
import org.geysermc.geyser.api.item.custom.v2.CustomItemDefinition
import org.geysermc.geyser.api.predicate.item.ItemMatchPredicate
import org.geysermc.geyser.api.predicate.item.ItemRangeDispatchPredicate
import org.geysermc.geyser.api.util.CreativeCategory
import org.geysermc.geyser.api.util.Identifier
import java.nio.file.Files
import java.nio.file.StandardCopyOption

object GeyserUtil {

    fun register(event: GeyserDefineCustomItemsEvent, itemName: String, cmd: Any, material: String) {
        val id = Identifier.of(material.lowercase())
        val brItem = CustomItemBedrockOptions.builder()
            .creativeCategory(CreativeCategory.ITEMS)
            .allowOffhand(true)
            .displayHandheld(material.isHandheld())

        val item = CustomItemDefinition.builder(Identifier.of("plushies", itemName.lowercase()), id)
            .bedrockOptions(brItem)
            .apply {
                when (cmd) {
                    is String -> predicate(ItemMatchPredicate.customModelData(0, cmd))
                    is Int, is Float -> predicate(ItemRangeDispatchPredicate.customModelData(0, (cmd as Number).toFloat()))
                    else -> return
                }
            }
            .displayName(itemName)

        event.register(id, item.build())
    }

    private fun String.isHandheld(): Boolean {
        return Tag.ITEMS_BREAKS_DECORATED_POTS.values
            .contains(Material.matchMaterial(this))
    }

    fun downloadBedrockPack() {
        val pack = WebUtil.getUrl("https://maltsburg.com/packs/bedrock") ?: return
        // download and replace bedrock pack
        pack.openStream().use { input ->
            Files.copy(input, GeyserApi.api().packDirectory().resolve("bedrock.zip"),
                StandardCopyOption.REPLACE_EXISTING)
        }
        pl.logger.info("Geyser pack downloaded")
    }
}