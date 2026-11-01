package co.akoot.plugins.plushies.geyser

import co.akoot.plugins.bluefox.util.Text.Companion.asString
import co.akoot.plugins.bluefox.util.WebUtil
import co.akoot.plugins.plushies.Plushies
import co.akoot.plugins.plushies.util.Util.pl
import org.bukkit.Tag
import org.bukkit.inventory.ItemStack
import org.geysermc.geyser.api.GeyserApi
import org.geysermc.geyser.api.event.lifecycle.GeyserDefineCustomItemsEvent
import org.geysermc.geyser.api.item.custom.v2.CustomItemBedrockOptions
import org.geysermc.geyser.api.item.custom.v2.CustomItemDefinition
import org.geysermc.geyser.api.item.custom.v2.CustomItemDefinitionRegisterException
import org.geysermc.geyser.api.predicate.PredicateStrategy
import org.geysermc.geyser.api.predicate.item.ItemMatchPredicate
import org.geysermc.geyser.api.predicate.item.ItemRangeDispatchPredicate
import org.geysermc.geyser.api.util.CreativeCategory
import org.geysermc.geyser.api.util.Identifier
import java.nio.file.Files
import java.nio.file.StandardCopyOption

object GeyserUtil {

    fun register(event: GeyserDefineCustomItemsEvent, itemStack: ItemStack, id: String) {
        val meta = itemStack.itemMeta
        val cmd = meta.customModelDataComponent
        val itemModel = meta.itemModel

        if (itemModel == null && !meta.hasCustomModelDataComponent()) return

        val brOptions = CustomItemBedrockOptions.builder()
            .displayHandheld(Tag.ITEMS_BREAKS_DECORATED_POTS.isTagged(itemStack.type))
            .creativeCategory(CreativeCategory.ITEMS)
            .creativeGroup("custom items")
            .icon(id)

        val data = CustomItemDefinition.builder(
            Identifier.of("plushies",id),
            Identifier.of(itemModel?.toString() ?: itemStack.type.name.lowercase()))
            .displayName(itemStack.effectiveName().asString())
            .bedrockOptions(brOptions)
            .predicateStrategy(PredicateStrategy.AND)
            .apply {
                when {
                    cmd.strings.isNotEmpty() ->
                        predicate(ItemMatchPredicate.customModelData(0, cmd.strings.first()))

                    cmd.floats.isNotEmpty() -> {
                        val value = cmd.floats.first()
                        predicate(ItemRangeDispatchPredicate.customModelData(0, value))
                        predicate(ItemRangeDispatchPredicate.customModelData(0, value + 1).negate())
                    }
                }
            }
            .build()

        try {
            event.register(Identifier.of(itemStack.type.name.lowercase()), data)
        } catch (e: CustomItemDefinitionRegisterException) {
            Plushies.instance.logger.warning(e.message)
        }
    }

    fun downloadBedrockPack() {
        try {
            val pack = WebUtil.getUrl("https://github.com/akootco/bedrock-textures/releases/latest/download/bedrock.zip") ?: return
            // download and replace bedrock pack
            pack.openStream().use { input ->
                Files.copy(
                    input, GeyserApi.api().packDirectory().resolve("bedrock.zip"),
                    StandardCopyOption.REPLACE_EXISTING
                )
            }
            pl.logger.info("Geyser pack downloaded")
        } catch (e: Exception) {
            pl.logger.warning("Failed to download Geyser pack")
        }
    }
}