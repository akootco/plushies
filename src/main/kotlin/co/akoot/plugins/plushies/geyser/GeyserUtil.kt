package co.akoot.plugins.plushies.geyser

import co.akoot.plugins.bluefox.util.WebUtil
import co.akoot.plugins.plushies.util.ResourcePack.packUrl
import org.bukkit.Tag
import org.bukkit.Material
import org.geysermc.geyser.api.GeyserApi
import org.geysermc.geyser.api.event.lifecycle.GeyserDefineCustomItemsEvent
import org.geysermc.geyser.api.item.custom.CustomItemData
import org.geysermc.geyser.api.item.custom.CustomItemOptions
import java.nio.file.Files
import java.nio.file.StandardCopyOption

object GeyserUtil {

    fun register(event: GeyserDefineCustomItemsEvent, itemName: String, customModelData: Int, material: String) {
        val data = CustomItemData.builder()
            .customItemOptions(CustomItemOptions.builder().customModelData(customModelData).build())
            // add item to creative category, so they show up in crafting book
            .creativeCategory(1) // this sucks, why is it always construction
            .name(itemName.lowercase())
            .allowOffhand(true)
            .displayHandheld(material.isHandheld())

        event.register("minecraft:${material.lowercase()}", data.build())
    }

    private fun String.isHandheld(): Boolean {
        return Tag.ITEMS_BREAKS_DECORATED_POTS.values
            .contains(Material.matchMaterial(this))
    }

    fun downloadBedrockPack() {
        val pack = WebUtil.getUrl(packUrl("bedrock")) ?: return
        // download and replace bedrock pack
        pack.openStream().use { input ->
            Files.copy(input, GeyserApi.api().packDirectory().resolve("bedrock.zip"), StandardCopyOption.REPLACE_EXISTING)
        }
    }
}