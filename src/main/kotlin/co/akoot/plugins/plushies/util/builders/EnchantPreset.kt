package co.akoot.plugins.plushies.util.builders

import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack

// TODO: info sucks. make it better, or else!!!!!!!!!!!!!!!


/**
 * Enchant builder
 *
 * @property itemStack
 * @constructor Create empty Enchant builder
 */
class EnchantPreset private constructor(private val itemStack: ItemStack) {

    private val mendUnbrk = mutableMapOf(
        Enchantment.MENDING to 1,
        Enchantment.UNBREAKING to 3
    )

    /**
     * Sword
     *
     * @return
     */
    fun sword(): EnchantPreset {
        ItemBuilder.builder(itemStack)
            .enchants(mutableMapOf(
                Enchantment.LOOTING to 3,
                Enchantment.SHARPNESS to 5,
                Enchantment.SWEEPING_EDGE to 3,
                Enchantment.KNOCKBACK to 2,
                Enchantment.FIRE_ASPECT to 2,
            )).build()
        return this
    }

    /**
     * Axe
     *
     * @param isFortune
     * @return
     */
    fun axe(isFortune: Boolean = false): EnchantPreset {
        ItemBuilder.builder(itemStack).enchants(mutableMapOf(
            Enchantment.SHARPNESS to 5,
            Enchantment.KNOCKBACK to 2,
            Enchantment.MENDING to 1,
            Enchantment.UNBREAKING to 3,
            Enchantment.EFFICIENCY to 5,
        ).apply {
            if (isFortune) {
                put(Enchantment.FORTUNE, 3)
            } else {
                put(Enchantment.SILK_TOUCH, 1)
            }
        }).build()
        return this
    }

    /**
     * Dig tool
     *
     * @param isFortune
     * @return
     */
    fun digTool(isFortune: Boolean = false): EnchantPreset {
        ItemBuilder.builder(itemStack).enchants(mutableMapOf(
            Enchantment.EFFICIENCY to 5,
        ).apply {
            if (isFortune) {
                put(Enchantment.FORTUNE, 3)
            } else {
                put(Enchantment.SILK_TOUCH, 1)
            }
        }).build()
        return this
    }

    /**
     * Helmet
     *
     * @return
     */
    fun helmet(): EnchantPreset {
        ItemBuilder.builder(itemStack).apply {
            enchants(
                mutableMapOf(
                    Enchantment.PROTECTION to 4,
                    Enchantment.RESPIRATION to 3,
                    Enchantment.AQUA_AFFINITY to 3
                )
            )
        }.build()
        return this
    }

    /**
     * Chestplate
     *
     * @return
     */
    fun chestplate(): EnchantPreset {
        ItemBuilder.builder(itemStack).apply {
            enchants(
                mutableMapOf(
                    Enchantment.UNBREAKING to 3,
                    Enchantment.PROTECTION to 4,
                )
            )
        }.build()
        return this
    }

    /**
     * Leggings
     *
     * @return
     */
    fun leggings(): EnchantPreset {
        ItemBuilder.builder(itemStack)
            .enchants(
                mutableMapOf(
                    Enchantment.SWIFT_SNEAK to 3,
                    Enchantment.PROTECTION to 4,
                )
            ).build()
        return this
    }


    /**
     * Boots
     *
     * @param frostWalker
     * @return
     */
    fun boots(frostWalker: Boolean = false): EnchantPreset {
        ItemBuilder.builder(itemStack).enchants(mutableMapOf(
            Enchantment.FEATHER_FALLING to 4,
            Enchantment.PROTECTION to 4,
            Enchantment.SOUL_SPEED to 4,
        ).apply {
            if (frostWalker) {
                put(Enchantment.FROST_WALKER, 2)
            } else {
                put(Enchantment.DEPTH_STRIDER, 3)
            }
        }).build()

        return this
    }
    /**
     * Build
     *
     * @return
     */
    fun build(): ItemStack {
        ItemBuilder.builder(itemStack)
            .enchants((itemStack.enchantments + mendUnbrk).toMutableMap())
            .build()
        return itemStack
    }

    companion object {
        fun builder(itemStack: ItemStack): EnchantPreset {
            return EnchantPreset(itemStack)
        }
    }
}