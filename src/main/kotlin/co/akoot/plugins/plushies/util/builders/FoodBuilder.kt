package co.akoot.plugins.plushies.util.builders

import io.papermc.paper.datacomponent.DataComponentTypes
import io.papermc.paper.datacomponent.item.*
import io.papermc.paper.datacomponent.item.consumable.ConsumeEffect
import io.papermc.paper.datacomponent.item.consumable.ItemUseAnimation
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

// TODO: info sucks. make it better, or else!!!!!!!!!!!!!!!

/**
 * Allows modification of an item's data.
 *
 * @property itemStack The `ItemStack` representing the item.
 *
 */
class FoodBuilder private constructor(private val itemStack: ItemStack) {

    private val cBuilder = Consumable.consumable()
    private val fBuilder = FoodProperties.food()

    /**
     * Sets the hunger and saturation values for the item. Optionally, the duration
     * of the consumption animation can be set.
     *
     * @param hunger The amount of hunger restored by this consumable item.
     * @param saturation The saturation level restored by this consumable item.
     * @param eatTime The duration of the eating animation in seconds. Defaults to 1.6 seconds.
     */
    fun hunger(hunger: Int, saturation: Float, eatTime: Float? = 1.6f): FoodBuilder {
        fBuilder.nutrition(hunger)
        fBuilder.saturation(saturation)
        eatTime?.let { cBuilder.consumeSeconds(it) }
        return this
    }

    /**
     * Sets the consumable item to be always edible, meaning the player can eat it even when not hungry.
     */
    fun isSnack(): FoodBuilder {
        fBuilder.canAlwaysEat(true)
        return this
    }

    /**
     * Sets the eating animation for the item.
     *
     * @param animation The [ItemUseAnimation] to be used during consumption.
     */
    fun animation(animation: ItemUseAnimation): FoodBuilder {
        cBuilder.animation(animation)
        return this
    }

    /**
     * Disables crumb particles while eating the item.
     */
    fun noCrumbs(): FoodBuilder {
        cBuilder.hasConsumeParticles(false)
        return this
    }

    /**
     * Sets the sound to be played while the item is being eaten.
     *
     * The sound is specified as a string, which allows for custom sounds from a resource pack.
     * @param sound The name of the sound to be played while eating the item.
     */
    fun eatSound(sound: String): FoodBuilder {
        cBuilder.sound(NamespacedKey.minecraft(sound))
        return this
    }

    /**
     * Sets the sound to be played after the item has been consumed.
     *
     * The sound is specified as a string, which allows for custom sounds from a resource pack.
     * @param sound The name of the sound to be played after consumption.
     */
    fun afterEatSound(sound: String): FoodBuilder {
        cBuilder.addEffect(ConsumeEffect.playSoundConsumeEffect(NamespacedKey.minecraft(sound)))
        return this
    }

    /**
     * Clears all effects that are currently applied to the player after the item is consumed.
     */
    fun clearEffects(): FoodBuilder {
        cBuilder.addEffect(ConsumeEffect.clearAllStatusEffects())
        return this
    }

    /**
     * Teleports the player to a random location within the specified range.
     *
     * @param range The distance (in blocks) the player can be teleported.
     */
    fun tp(range: Float): FoodBuilder {
        cBuilder.addEffect(ConsumeEffect.teleportRandomlyEffect(range))
        return this
    }

    /**
     * Adds a potion effect to be applied when the item is consumed.
     *
     * @param effectType The type of [PotionEffect] to be applied.
     * @param duration The duration (in seconds) of the potion effect.
     * @param level The level of the potion effect.
     * @param chance The chance (from 0.0 to 1.0) that the effect will be applied.
     */
    fun addEffect(
        effectType: PotionEffectType,
        duration: Int,
        level: Int,
        chance: Float
    ): FoodBuilder {
        val effect = PotionEffect(effectType,
            duration * 20,
            level - 1,
            true, true, true)
        cBuilder.addEffect(ConsumeEffect.applyStatusEffects(listOf(effect), chance))
        return this
    }

    /**
     * Builds and returns the final `ItemStack`.
     *
     * @return The final `ItemStack` with all the changes made to it.
     */
    fun build(): ItemStack {
        itemStack.setData(DataComponentTypes.FOOD, FoodProperties.food().build())
        itemStack.setData(DataComponentTypes.CONSUMABLE, Consumable.consumable().build())
        return itemStack
    }


    companion object {
        fun builder(itemStack: ItemStack): FoodBuilder {
            return FoodBuilder(itemStack)
        }
    }
}