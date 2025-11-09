package co.akoot.plugins.plushies.util.builders

import co.akoot.plugins.bluefox.api.Kolor
import co.akoot.plugins.bluefox.extensions.removePDC
import co.akoot.plugins.bluefox.extensions.setPDC
import co.akoot.plugins.bluefox.util.Text
import co.akoot.plugins.plushies.Plushies.Companion.key
import co.akoot.plugins.plushies.listeners.tasks.Throwable.Companion.axeKey
import co.akoot.plugins.plushies.util.Items.hitSoundKey
import com.destroystokyo.paper.profile.ProfileProperty
import io.papermc.paper.datacomponent.DataComponentType
import io.papermc.paper.datacomponent.DataComponentTypes
import io.papermc.paper.datacomponent.item.*
import io.papermc.paper.datacomponent.item.consumable.ItemUseAnimation
import io.papermc.paper.registry.RegistryAccess
import io.papermc.paper.registry.RegistryKey
import io.papermc.paper.registry.tag.TagKey
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import net.kyori.adventure.util.TriState
import org.bukkit.*
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.block.BlockType
import org.bukkit.damage.DamageType
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlotGroup
import org.bukkit.inventory.ItemRarity
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.BookMeta
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.potion.PotionEffect
import java.nio.charset.StandardCharsets
import java.util.*

// TODO: info sucks. make it better, or else!!!!!!!!!!!!!!!

/**
 * Allows modification of an item's data.
 *
 * @property itemStack The `ItemStack` representing the item.
 *
 */
class ItemBuilder private constructor(private var itemStack: ItemStack) {
    constructor(material: Material) : this(ItemStack(material))
    /**
     * Sets custom model data for an item.
     *
     * @param value  The custom model data value, either an Int
     *               or a String.
     * @return       The updated item with the applied custom model data.
     */
    fun customModelData(value: Any): ItemBuilder {
        val builder = CustomModelData.customModelData()

        val strValue = value.toString()
        val intValue = strValue.toIntOrNull()

        builder.apply {
            if (intValue != null) addFloat(intValue.toFloat())
            else builder.addString(strValue.lowercase())
        }

        itemStack.setData(DataComponentTypes.CUSTOM_MODEL_DATA, builder.build())
        return this
    }


    fun hitSound(sound: String): ItemBuilder {
        if (sound in listOf("none", "-c", "null")) { removepdc(hitSoundKey) }
        else { pdc(hitSoundKey, sound) }
        return this
    }

    /**
     * Adds additional lines to an item's tooltip (lore).
     *
     * @param lore  A list of strings representing the lines of lore to add to the item.
     * This can include Minecraft color and formatting codes (e.g., `&a` for green, `&l` for bold).
     * @return      The updated item with the applied lore.
     */
    fun lore(lore: List<Component>?): ItemBuilder {
        if (!lore.isNullOrEmpty()) {
            val loreBuilder = ItemLore.lore()
            for (line in lore) {
                loreBuilder.addLine(Text(line)
                    .color(Kolor.TEXT).component
                    .decoration(TextDecoration.ITALIC, false))
            }
            val finalLore = loreBuilder.build()
            itemStack.setData(DataComponentTypes.LORE, finalLore)
        }
        return this
    }

    fun filler(): ItemBuilder {
        itemName(Text().component)
        itemModel("air")
        hideTooltip()
        return this
    }

    /**
     * Adds an enchantment glint to the item without requiring an enchantment.
     *
     * @return The updated item with the enchantment glint applied.
     */
    fun glint(): ItemBuilder {
        itemStack.setData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true)
        return this
    }

    /**
     * Add enchant
     * - will overrride current enchantments on item
     * @param enchantment
     * @param level
     * @return
     */
    fun enchant(enchantment: Enchantment, level: Int = 1): ItemBuilder {
        val enchant = ItemEnchantments.itemEnchantments()
        enchant.add(enchantment, level)
        itemStack.setData(DataComponentTypes.ENCHANTMENTS, enchant)
        return this
    }

    /**
     * Add list of enchants
     * - will overrride current enchantments on item
     * @param enchantments
     * @return
     */
    fun enchants(enchantments: MutableMap<Enchantment, Int>): ItemBuilder {
        val enchants = ItemEnchantments.itemEnchantments()
        enchants.addAll(enchantments)
        itemStack.setData(DataComponentTypes.ENCHANTMENTS, enchants)
        return this
    }

    /**
     * Add enchant
     * - will NOT overrride current enchantments on item
     * @param enchantment
     * @param level
     * @return
     */
    fun addEnchant(enchantment: Enchantment, level: Int = 1): ItemBuilder {
        itemStack.addUnsafeEnchantment(enchantment, level)
        return this
    }

    /**
     * Add list of enchants
     * - will NOT overrride current enchantments on item
     * @param enchantments
     * @return
     */
    fun addEnchants(enchantments: MutableMap<Enchantment, Int>): ItemBuilder {
        itemStack.addUnsafeEnchantments(enchantments)
        return this
    }


    /**
     * Dye
     *
     * @param color
     * @param shownInTooltip
     * @return
     */
    fun dye(color: Color): ItemBuilder {
        val dye = DyedItemColor.dyedItemColor()
        dye.color(color)
        itemStack.setData(DataComponentTypes.DYED_COLOR, dye.build())
        return this
    }

    /**
     * Attribute
     *
     * @param attribute
     * @param modifier
     * @param slot
     * @param shownInTooltip
     * @return
     */
    fun attribute(attribute: Attribute, modifier: AttributeModifier, slot: EquipmentSlotGroup): ItemBuilder {
        val att = ItemAttributeModifiers.itemAttributes()
        att.addModifier(attribute, modifier, slot)

        itemStack.setData(DataComponentTypes.ATTRIBUTE_MODIFIERS, att.build())
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
    fun itemModel(model: String): ItemBuilder {
        NamespacedKey.fromString(model)?.let { itemStack.setData(DataComponentTypes.ITEM_MODEL, it) }
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
    fun tooltipStyle(namespace: String, id: String): ItemBuilder {
        val model = NamespacedKey(namespace, id)
        itemStack.setData(DataComponentTypes.TOOLTIP_STYLE, model)
        return this
    }

    /**
     * Written book
     *
     * @param title
     * @param author
     * @param pages
     * @return
     */
    fun writtenBook(pages: MutableList<Component>, title: String = "Book", author: String = "Maltsburg", generation: Int = 0): ItemBuilder {
        itemStack.setData(DataComponentTypes.WRITTEN_BOOK_CONTENT,
            WrittenBookContent.writtenBookContent(title, author)
                .generation(generation)
                .addPages(pages)
                .build()
        )
        return this
    }

    /**
     * Written book
     *
     * @param page
     * @return
     * @param title
     * @param author
     * @param generation
     */
    fun writtenBook(page: Component, title: String = "Book", author: String = "Maltsburg", generation: Int = 0): ItemBuilder {
        itemStack.setData(DataComponentTypes.WRITTEN_BOOK_CONTENT,
            WrittenBookContent.writtenBookContent(title, author)
                .generation(generation)
                .addPage(page)
                .build()
        )
        return this
    }

    /**
     * Writable book
     *
     * @param pages
     * @return
     */
    fun writableBook(pages: MutableList<String>): ItemBuilder {
        itemStack.setData(DataComponentTypes.WRITABLE_BOOK_CONTENT,
            WritableBookContent.writeableBookContent()
                .addPages(pages)
                .build()
        )
        return this
    }

    /**
     * Writable book
     *
     * @param page
     * @return
     */
    fun writableBook(page: String): ItemBuilder {
        itemStack.setData(DataComponentTypes.WRITABLE_BOOK_CONTENT,
            WritableBookContent.writeableBookContent()
                .addPage(page)
                .build()
        )
        return this
    }

    /**
     * Copies data of the item to the builder's item.
     *
     * @param itemStack The item to copy.
     * @return The modified `Item` instance.
     */
    fun copyOf(itemStack: ItemStack): ItemBuilder {
        this.itemStack.copyDataFrom(itemStack) { true }
        return this
    }

    fun copyOf(itemStack: ItemStack, vararg components: DataComponentType): ItemBuilder {
        val allowed = components.toSet()
        this.itemStack.copyDataFrom(itemStack) { type -> allowed.contains(type) }
        return this
    }

    /**
     * Copies data of the book to the builder's item.
     *
     * @param book The source book.
     * @return The modified `Item` instance.
     */
    fun copyToBook(book: ItemStack): ItemBuilder {
        val bookMeta = book.itemMeta as? BookMeta ?: return this
        if (book.type == Material.WRITTEN_BOOK) {
            val pages: MutableList<String> = bookMeta.pages().map { page ->
                PlainTextComponentSerializer.plainText().serialize(page)
            }.toMutableList()
            writableBook(pages)
        }
        return this
    }

    /**
     * Copies data of the book to the builder's item.
     *
     * @param book The source book.
     * @return The modified `Item` instance.
     */
    fun copyOfBook(book: ItemStack): ItemBuilder {
        val bookMeta = book.itemMeta as? BookMeta ?: return this
        val pages: MutableList<Component> = bookMeta.pages()
        bookMeta.title?.let { bookMeta.author?.let { it1 -> writtenBook(pages, it, it1) } }
        return this
    }

    /**
     * if set, protects the holder from death, similar to a `TOTEM_OF_UNDYING`.
     *
     * @param shouldProtect If true, add protection (default). If false, remove protection.
     * @return The modified `Item` instance.
     */
    fun deathProtection(shouldProtect: Boolean = true): ItemBuilder {
        if (shouldProtect) {
            itemStack.setData(DataComponentTypes.DEATH_PROTECTION, DeathProtection.deathProtection().build())
        } else if (itemStack.type == Material.TOTEM_OF_UNDYING){
            unsetData(DataComponentTypes.DEATH_PROTECTION)
        }
        return this
    }

    /**
     * Jukebox song
     *
     * @param song
     * @return
     */
    fun jukeboxSong(song: JukeboxSong): ItemBuilder {
        itemStack.setData(DataComponentTypes.JUKEBOX_PLAYABLE, JukeboxPlayable.jukeboxPlayable(song))
        return this
    }

    /**
     * Instrument
     *
     * @param instrument
     * @return
     */
    fun instrument(instrument: MusicInstrument): ItemBuilder {
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
    fun maxDurability(durability: Int): ItemBuilder {
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
    fun damageResistance(damageType: TagKey<DamageType>): ItemBuilder {
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
    fun setTool(blockTagKey: TagKey<BlockType>, miningSpeed: Float = 1.0F): ItemBuilder {
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

    fun setWeapon(damage: Double = 2.0, speed: Double = 1.6): ItemBuilder {
        val attributeModifiers: ItemAttributeModifiers = ItemAttributeModifiers.itemAttributes()
            .addModifier(Attribute.ATTACK_DAMAGE,AttributeModifier(
                key("attack.dmg"),
                damage,
                AttributeModifier.Operation.ADD_NUMBER
            ))
            .addModifier(Attribute.ATTACK_SPEED,AttributeModifier(
                key("attack.spd"),
                speed,
                AttributeModifier.Operation.ADD_NUMBER
            ))
            .build()

        itemStack.setData(DataComponentTypes.ATTRIBUTE_MODIFIERS, attributeModifiers)

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

    /**
     * If set, it will completely hide whole item tooltip (that includes item name).
     *
     * @return
     */
    fun hideTooltip(): ItemBuilder {
        val meta = itemStack.itemMeta
        meta.isHideTooltip = true
        itemStack.setItemMeta(meta)
        return this
    }

    /**
     * Sets the item to leave behind after use.
     * For example, water buckets leave behind an empty bucket when used.
     *
     * @param item  The `ItemStack` that will remain after the item is consumed or used.
     * @return      The updated `Item` instance with the "use remainder" property applied.
     */
    fun useRemainder(item: ItemStack): ItemBuilder {
        itemStack.setData(DataComponentTypes.USE_REMAINDER, UseRemainder.useRemainder(item))
        return this
    }

    /**
     * Controls the maximum stacking size of this item. Values greater than 1 are mutually exclusive with the `MAX_DAMAGE` component.
     *
     * @param stackSize  The max number of items that can be stacked together.
     * @return           The updated `Item` with the specified stack size applied.
     */
    fun stackSize(stackSize: Int): ItemBuilder {
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
     * @param name The name to set on the item.
     * @return The updated `Item` with the specified name applied.
     */
    fun itemName(name: Component): ItemBuilder {
        if (itemStack.type in listOf(Material.PLAYER_HEAD, Material.WRITTEN_BOOK, Material.POTION)) {
            itemStack.setData(DataComponentTypes.CUSTOM_NAME, name.decoration(TextDecoration.ITALIC, false))
        } else {
            itemStack.setData(DataComponentTypes.ITEM_NAME, name)
        }
        return this
    }

    /**
     * Sets a cooldown for the item.
     *
     * @param cooldown the duration in seconds; must be positive
     * @return The updated `Item` with the cooldown applied.
     */
    fun cooldown(cooldown: Float = 10.0f): ItemBuilder {
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

    fun headTexture(textureId: String): ItemBuilder {
        val textureUrl = "http://textures.minecraft.net/texture/$textureId"
        val json = "{\"textures\":{\"SKIN\":{\"url\":\"$textureUrl\"}}}"
        val encodedTexture = Base64.getEncoder().encodeToString(json.toByteArray(StandardCharsets.UTF_8))
        itemStack.setData(DataComponentTypes.PROFILE, ResolvableProfile.resolvableProfile()
            .addProperty(ProfileProperty("textures", encodedTexture))
            .uuid(UUID.fromString("f592cd5e-ca73-4612-a962-9f3ec57dc108"))
        )
        return this
    }

    fun playerHead(player: Player): ItemBuilder {
        if (!itemStack.type.name.endsWith("_HEAD")) return this
        val meta = itemStack.itemMeta

        val headMeta = meta as SkullMeta
        val profile = Bukkit.createProfile(player.uniqueId, player.name)
        headMeta.playerProfile = profile
        itemStack.setItemMeta(headMeta)
        return this
    }

    fun playerHead(player: OfflinePlayer): ItemBuilder {
        if (!itemStack.type.name.endsWith("_HEAD")) return this
        val meta = itemStack.itemMeta

        val headMeta = meta as SkullMeta
        val profile = Bukkit.createProfile(player.uniqueId, player.name)
        headMeta.playerProfile = profile
        itemStack.setItemMeta(headMeta)
        return this
    }

    fun headSound(sound: String): ItemBuilder {
        itemStack.setData(DataComponentTypes.NOTE_BLOCK_SOUND, NamespacedKey.minecraft(sound))
        return this
    }

    /**
     * Controls the color of the item name based on its rarity.
     *
     * @param rarity The rarity of the item.
     * @return The updated `Item` with the specified rarity applied.
     */
    fun rarity(rarity: ItemRarity): ItemBuilder {
        itemStack.setData(DataComponentTypes.RARITY, rarity)
        return this
    }

    /**
     * Marks the item as throwable and optionally adds a channeling effect.
     *
     * @param smite If `true`, the item will summon lightning when it hits an entity.
     * @return The updated `Item` with the throwable behavior applied.
     */
    fun throwable(): ItemBuilder {
        FoodBuilder.builder(pdc(axeKey).build())
            .animation(ItemUseAnimation.SPEAR)
            .hunger(0,0f,0.1f)
            .isSnack()
            .noCrumbs()
            .eatSound("_")
            .build()
        return this
    }

    /**
     * Sets persistent data on the item.
     *
     * @param key The `NamespacedKey` that identifies the data.
     * @param value The value to store.
     * @return The updated `Item` with the persistent data applied.
     */
    fun pdc(key: NamespacedKey, value: Any = ""): ItemBuilder {
        val meta = itemStack.itemMeta
        when (value) {
            is String -> meta.setPDC<String>(key, value)
            is Boolean -> meta.setPDC<Boolean>(key, value)
        }

        itemStack.setItemMeta(meta)
        return this
    }

    /**
     * removepdc
     *
     * @param key
     * @return
     */
    fun removepdc(key: NamespacedKey): ItemBuilder {
        val meta = itemStack.itemMeta
        meta.removePDC(key)
        itemStack.setItemMeta(meta)
        return this
    }

    /**
     * Unset data
     *
     * @param dataType
     * @return
     */
    fun unsetData(dataType: DataComponentType): ItemBuilder {
        itemStack.unsetData(dataType)
        return this
    }

    fun resetData(dataType: DataComponentType): ItemBuilder {
        itemStack.resetData(dataType)
        return this
    }

    fun potion(hex: String, effect: PotionEffect? = null): ItemBuilder {
        if (itemStack.type != Material.POTION) return this

        val potion = PotionContents.potionContents().apply {
            if (effect != null) { addCustomEffect(effect) }
            if (hex.length == 6) {
                customColor(Color.fromRGB(
                        hex.substring(0, 2).toInt(16),
                        hex.substring(2, 4).toInt(16),
                        hex.substring(4, 6).toInt(16)
                ))
            }
        }

        itemStack.setData(DataComponentTypes.POTION_CONTENTS, potion.build())
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
        fun builder(itemStack: ItemStack) = ItemBuilder(itemStack)
        fun builder(material: Material) = ItemBuilder(material)
    }
}