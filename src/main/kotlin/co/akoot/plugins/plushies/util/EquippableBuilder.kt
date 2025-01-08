package co.akoot.plugins.plushies.util

import io.papermc.paper.datacomponent.DataComponentTypes
import io.papermc.paper.datacomponent.item.*
import org.bukkit.NamespacedKey
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack

// TODO: info sucks. make it better, or else!!!!!!!!!!!!!!!

/**
 * Equippable builder
 *
 * @property itemStack
 * @constructor
 *
 * @param slot
 */
class EquippableBuilder private constructor(private val itemStack: ItemStack, slot: EquipmentSlot) {

    private val eBuilder = Equippable.equippable(slot)


    /**
     * Damage on hurt
     *
     * @return
     */
    fun unbreakable(): EquippableBuilder {
        eBuilder.damageOnHurt(false)

        ItemBuilder.builder(itemStack)
            .stackSize(1)
            .build()

        return this
    }

    /**
     * Model
     *
     * @param id
     * @return
     */
    fun model(id: NamespacedKey? = null): EquippableBuilder {
        eBuilder.assetId(id)
        return this
    }

    /**
     * Swappable
     *
     * @param canSwap
     * @return
     */
    fun swappable(canSwap: Boolean = true): EquippableBuilder {
        eBuilder.swappable(canSwap)
        return this
    }

    /**
     * Dispensable
     *
     * @param dispensable
     * @return
     */
    fun dispensable(dispensable: Boolean = true): EquippableBuilder {
        eBuilder.dispensable(dispensable)
        return this
    }

    /**
     * Camera overlay
     *
     * @param id
     * @return
     */
    fun cameraOverlay(id: NamespacedKey): EquippableBuilder {
        eBuilder.cameraOverlay(id)
        return this
    }

    /**
     * Equip sound
     *
     * @param sound
     * @return
     */
    fun equipSound(sound: String): EquippableBuilder {
        eBuilder.equipSound(NamespacedKey.minecraft(sound))
        return this
    }

    /**
     * Sets the item to act as an elytra (glider).
     *
     * @return The updated `Item` with the glider behavior applied.
     */
    fun glider(): EquippableBuilder {
        itemStack.setData(DataComponentTypes.GLIDER)
        return this
    }

    /**
     * Builds and returns the final `ItemStack`.
     *
     * @return The final `ItemStack` with all the changes made to it.
     */
    fun build(): ItemStack {
        itemStack.setData(DataComponentTypes.EQUIPPABLE, eBuilder)
        return itemStack
    }


    companion object {
        fun builder(itemStack: ItemStack, slot: EquipmentSlot): EquippableBuilder {
            return EquippableBuilder(itemStack, slot)
        }
    }
}