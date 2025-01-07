package co.akoot.plugins.plushies.util

import com.destroystokyo.paper.profile.ProfileProperty
import io.papermc.paper.datacomponent.DataComponentTypes
import io.papermc.paper.datacomponent.item.*
import io.papermc.paper.registry.RegistryAccess
import io.papermc.paper.registry.RegistryKey
import io.papermc.paper.registry.tag.TagKey
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import net.kyori.adventure.util.TriState
import org.bukkit.*
import org.bukkit.block.BlockType
import org.bukkit.damage.DamageType
import org.bukkit.inventory.ItemRarity
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType
import java.nio.charset.StandardCharsets
import java.util.*

// TODO: info sucks. make it better, or else!!!!!!!!!!!!!!!

/**
 * Allows modification of an item's data.
 *
 * @property itemStack The `ItemStack` representing the item.
 *
 * @param amount The number of items in the stack. Defaults to 1.
 */
class Item private constructor(private val itemStack: ItemStack, amount: Int = 1) {
    private val container: PersistentDataContainer?

    init {
        val itemMeta = itemStack.itemMeta
        itemStack.amount = amount
        this.container = itemMeta?.persistentDataContainer
    }

    /**
     * Creates custom model data for an item, allowing optional parameters for color and model.
     *
     * @param float  Required. The threshold value used by the texture pack to determine
     *               if the item should have a different texture or model.
     * @param color  Optional. The color to be added to the custom model data.
     * @param model  Optional. The string representing the model to be added.
     * @return       The updated item with the applied custom model data.
     */
    fun customModelData(float: Float, color: Color? = null, model: String? = null): Item {
        val customModelDataBuilder = CustomModelData.customModelData().addFloat(float)
        color?.let { customModelDataBuilder.addColor(it) }
        model?.let { customModelDataBuilder.addString(it) }
        itemStack.setData(DataComponentTypes.CUSTOM_MODEL_DATA, customModelDataBuilder.build())
        return this
    }

    /**
     * Adds additional lines to an item's tooltip (lore).
     *
     * @param lore  A list of strings representing the lines of lore to add to the item.
     * This can include Minecraft color and formatting codes (e.g., `&a` for green, `&l` for bold).
     * @return      The updated item with the applied lore.
     */
    fun lore(lore: List<String?>?): Item {
        if (!lore.isNullOrEmpty()) {
            val loreBuilder = ItemLore.lore()
            for (line in lore) {
                loreBuilder.addLine(
                    LegacyComponentSerializer.legacyAmpersand()
                        .deserialize(line!!)
                        .decoration(TextDecoration.ITALIC, false)
                )
            }
            val finalLore = loreBuilder.build()
            itemStack.setData(DataComponentTypes.LORE, finalLore)
        }
        return this
    }

    /**
     * Adds an enchantment glint to the item without requiring an enchantment.
     *
     * @return The updated item with the enchantment glint applied.
     */
    fun glint(): Item {
        itemStack.setData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true)
        return this
    }

    /**
     * Sets the model for the item.
     *
     * The model is found in the resource pack at:
     * - `/assets/<namespace>/models/item/<id>`.
     *
     * @param namespace The name of the assets sub-folder.
     * @param id The model ID.
     * @return the updated `Item`.
     */
    fun itemModel(namespace: String, id: String): Item {
        val model = NamespacedKey(namespace, id)
        itemStack.setData(DataComponentTypes.ITEM_MODEL, model)
        return this
    }

    /**
     * Configures the tooltip style for this item.
     *
     * Tooltip assets are located at:
     * - /assets/`namespace`/textures/gui/sprites/tooltip/`id`_background
     * - /assets/`namespace`/textures/gui/sprites/tooltip/`id`_frame
     *
     * @param namespace the name of the assets sub-folder
     * @param id the tooltip ID
     * @return the updated `Item` instance
     */
    fun tooltipStyle(namespace: String, id: String): Item {
        val model = NamespacedKey(namespace, id)
        itemStack.setData(DataComponentTypes.TOOLTIP_STYLE, model)
        return this
    }

    /**
     * if set, protects the holder from death, similar to a `TOTEM_OF_UNDYING`.
     *
     * @return The modified `Item` instance.
     */
    fun deathProtection(): Item {
        itemStack.setData(DataComponentTypes.DEATH_PROTECTION, DeathProtection.deathProtection().build())
        return this
    }

    /**
     * Jukebox song
     *
     * @param song
     * @return
     */
    fun jukeboxSong(song: JukeboxSong): Item {
        itemStack.setData(DataComponentTypes.JUKEBOX_PLAYABLE, JukeboxPlayable.jukeboxPlayable(song))
        return this
    }

    /**
     * Instrument
     *
     * @param instrument
     * @return
     */
    fun instrument(instrument: MusicInstrument): Item {
        if (itemStack.type != Material.GOAT_HORN) return this

        itemStack.setData(DataComponentTypes.INSTRUMENT, instrument)
        return this
    }

    /**
     * Max durability
     *
     * @param durability
     * @return
     */
    fun maxDurability(durability: Int): Item {
        itemStack.setData(DataComponentTypes.MAX_DAMAGE, durability)
        return this
    }

    /**
     * Makes the item immune to specified damage types.
     *
     * If this item is damaged by one of the specified damage types, it will not take any damage.
     *
     * @param damageType The tag for the damage types this item is immune to, such as `DamageTypeTagKeys.IS_FIRE`.
     * @return The modified `Item` instance.
     */
    fun damageResistance(damageType: TagKey<DamageType>): Item {
        itemStack.setData(DataComponentTypes.DAMAGE_RESISTANT, DamageResistant.damageResistant(damageType))
        return this
    }

    /**
     * Sets the item to work as a tool with specific behavior.
     *
     * @param blockTagKey The tag for the blocks this tool can mine, like `MINEABLE_PICKAXE`.
     * @param miningSpeed The mining speed. Default is `1.0F` (relative to the item type).
     * @return The modified `Item` with the new tool behavior.
     */
    fun setTool(blockTagKey: TagKey<BlockType>, miningSpeed: Float = 1.0F): Item {
        val tool = Tool.tool()

        val blockTag = RegistryAccess.registryAccess()
            .getRegistry(RegistryKey.BLOCK)
            .getTag(blockTagKey)

        tool.addRule(
            Tool.rule(
                blockTag,
                miningSpeed,
                TriState.TRUE
            )
        )

        itemStack.setData(DataComponentTypes.TOOL, tool.build())
        return this
    }

    /**
     * If set, the item will not lose any durability when used.
     *
     * @param shownInTooltip Optional. Determines whether the "Unbreakable" property is shown in the item's tooltip.
     * Defaults to `true`.
     *
     * @return The modified `Item`  with the unbreakable property applied.
     */
    fun unbreakable(shownInTooltip: Boolean? = true): Item {
        val tooltipBuilder = Unbreakable.unbreakable()
        shownInTooltip?.let { tooltipBuilder.showInTooltip(it) }
        itemStack.setData(DataComponentTypes.UNBREAKABLE, tooltipBuilder.build())
        return this
    }

    /**
     * If set, it will completely hide whole item tooltip (that includes item name).
     *
     * @return
     */
    fun hideTooltip(): Item {
        itemStack.setData(DataComponentTypes.HIDE_TOOLTIP)
        return this
    }

    /**
     * Sets the item to leave behind after use.
     * For example, water buckets leave behind an empty bucket when used.
     *
     * @param item  The `ItemStack` that will remain after the item is consumed or used.
     * @return      The updated `Item` instance with the "use remainder" property applied.
     */
    fun useRemainder(item: ItemStack): Item {
        itemStack.setData(DataComponentTypes.USE_REMAINDER, UseRemainder.useRemainder(item))
        return this
    }

    /**
     * Controls the maximum stacking size of this item. Values greater than 1 are mutually exclusive with the `MAX_DAMAGE` component.
     *
     * @param stackSize  The max number of items that can be stacked together.
     * @return           The updated `Item` with the specified stack size applied.
     */
    fun stackSize(stackSize: Int): Item {
        itemStack.setData(DataComponentTypes.MAX_STACK_SIZE, stackSize)
        return this
    }

    /**
     * Sets a name for the item.
     *
     * If the item is a `PLAYER_HEAD`, it automatically sets a `CUSTOM_NAME` because the regular `ITEM_NAME` isn't visible.
     *
     * **Advantages of using `ITEM_NAME`:**
     * - Can't be changed or removed in Anvil.
     * - Does not show labels where applicable (for example: banner markers, names in item frames)
     *
     * @param name The name to set on the item. This can include Minecraft color and formatting codes (e.g., `&a` for green, `&l` for bold).
     * @return The updated `Item` with the specified name applied.
     */
    fun itemName(name: String): Item {
        if (itemStack.type.name.endsWith("_HEAD")) {
            itemStack.setData(
                DataComponentTypes.CUSTOM_NAME,
                LegacyComponentSerializer.legacyAmpersand().deserialize(name).decoration(TextDecoration.ITALIC, false)
            )
            return this
        }
        itemStack.setData(DataComponentTypes.ITEM_NAME, LegacyComponentSerializer.legacyAmpersand().deserialize(name))
        return this
    }

    /**
     * Sets a cooldown for the item.
     *
     * @param cooldown the duration in seconds; must be positive
     * @return The updated `Item` with the cooldown applied.
     */
    fun cooldown(cooldown: Float = 10.0f): Item {
        if (cooldown > 0) {
            itemStack.setData(DataComponentTypes.USE_COOLDOWN, UseCooldown.useCooldown(cooldown))
        }
        return this
    }

    /**
     * Changes the texture of a `PLAYER_HEAD`.
     *
     * You can upload your own texture at [Mineskin.org](https://mineskin.org).
     * @param textureId The texture ID to use. For example: `"957fd56ca15978779324df519354b6639a8d9bc1192c7c3de925a329baef6c"`.
     *                  This will make the full URL: `http://textures.minecraft.net/texture/$textureId`.
     *
     * @return The updated `Item` with the texture applied.
     */
    fun headTexture(textureId: String): Item {
        if (!itemStack.type.name.endsWith("_HEAD")) return this

        val headMeta = itemStack.itemMeta as SkullMeta
        val profile = Bukkit.createProfile(UUID.fromString("f592cd5e-ca73-4612-a962-9f3ec57dc108"), "")
        val textureUrl = "http://textures.minecraft.net/texture/$textureId"
        val json = "{\"textures\":{\"SKIN\":{\"url\":\"$textureUrl\"}}}"
        val encodedTexture = Base64.getEncoder().encodeToString(json.toByteArray(StandardCharsets.UTF_8))
        profile.setProperty(ProfileProperty("textures", encodedTexture))
        headMeta.playerProfile = profile
        itemStack.setItemMeta(headMeta)
        return this
    }

    /**
     * Controls the color of the item name based on its rarity.
     *
     * @param rarity The rarity of the item.
     * @return The updated `Item` with the specified rarity applied.
     */
    fun rarity(rarity: ItemRarity): Item {
        itemStack.setData(DataComponentTypes.RARITY, rarity)
        return this
    }

    /**
     * Marks the item as throwable and optionally adds a channeling effect.
     *
     * @param smite If `true`, the item will summon lightning when it hits an entity.
     * @return The updated `Item` with the throwable behavior applied.
     */
    fun throwable(smite: Boolean = false): Item {
        val type = if (smite) "smite" else "default"
        pdc(NamespacedKey("plushies", "throwable"), type)
        return this
    }

    /**
     * Sets persistent data on the item.
     *
     * @param key The `NamespacedKey` that identifies the data.
     * @param value The value to store.
     * @return The updated `Item` with the persistent data applied.
     */
    fun pdc(key: NamespacedKey, value: Any): Item {
        val meta = itemStack.itemMeta ?: return this

        val container = meta.persistentDataContainer

        when (value) {
            is String -> container.set(key, PersistentDataType.STRING, value)
            is Boolean -> container.set(key, PersistentDataType.BOOLEAN, value)
            is Long -> container.set(key, PersistentDataType.LONG, value)
            is Float -> container.set(key, PersistentDataType.FLOAT, value)
            is Int -> container.set(key, PersistentDataType.INTEGER, value)
        }

        itemStack.setItemMeta(meta)
        return this
    }

    /**
     * Builds and returns the final `ItemStack`.
     *
     * @return The final `ItemStack` with all the changes made to it.
     */
    fun build(): ItemStack {
        return itemStack
    }

    companion object {
        fun builder(itemStack: ItemStack, amount: Int = 1): Item {
            return Item(itemStack, amount)
        }
    }
}