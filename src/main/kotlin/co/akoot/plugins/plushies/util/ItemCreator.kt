package co.akoot.plugins.plushies.util

import co.akoot.plugins.bluefox.api.FoxConfig
import co.akoot.plugins.bluefox.util.Text
import co.akoot.plugins.plushies.Plushies.Companion.customMusicDiscConfig
import co.akoot.plugins.plushies.Plushies.Companion.key
import co.akoot.plugins.plushies.Plushies.Companion.pluginEnabled
import co.akoot.plugins.plushies.util.Items.pendingHeads
import co.akoot.plugins.plushies.util.Items.placeableKey
import co.akoot.plugins.plushies.util.builders.EquippableBuilder
import co.akoot.plugins.plushies.util.builders.FoodBuilder
import co.akoot.plugins.plushies.util.builders.ItemBuilder
import io.papermc.paper.datacomponent.DataComponentTypes
import io.papermc.paper.datacomponent.item.consumable.ItemUseAnimation
import me.arcaniax.hdb.api.HeadDatabaseAPI
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.Registry
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemRarity
import org.bukkit.inventory.ItemStack

object ItemCreator {

    fun createItem(config: FoxConfig, path: String, namespacedKey: NamespacedKey): ItemStack? {
        val material = when (config) {
            customMusicDiscConfig -> Material.MUSIC_DISC_11
            else -> config.getString("$path.material")
                ?.let(Material::getMaterial)
                ?: return null
        }

        return ItemStack(material)
            .let { itemData(config, path, it, namespacedKey) }
            ?.let { equippable(config, path, it) }
            ?.let { food(config, path, it) }
    }

    private fun itemData(config: FoxConfig, path: String, itemStack: ItemStack, namespacedKey: NamespacedKey): ItemStack? {
        return ItemBuilder.builder(itemStack).apply {

            if (config.getBoolean("$path.isBlock") == true) {
                val hdb = config.getString("$path.hdb")
                val textures = config.getString("$path.textures")
                val cmd = config.getString("$path.customModelData")

                when {
                    hdb != null-> { pdc(blockKey, "$path|$hdb") }

                    textures != null -> {
                        pdc(blockKey, "$path|$textures")
                        itemModel( "player_head")
                    }
                    cmd != null -> pdc(texturedkKey, "$path|$cmd")
                    else -> pdc(blockKey, path)
                }

                rarity(ItemRarity.COMMON)
            }

            config.getString("$path.hdb")?.let { hdb ->
                if (pluginEnabled("HeadDatabase")) {
                    HeadDatabaseAPI().getItemHead(hdb)?.let { head ->
                        copyOf(head, DataComponentTypes.PROFILE)
                        itemModel("player_head")
                    }
                } else {
                    pendingHeads += Items.PendingHead(config, path, namespacedKey)
                    println(path)
                    return null
                }
            }

            pdc(namespacedKey, path)

            // set item as placeable
            config.getBoolean("$path.placeable")?.takeIf { it }?.let { pdc(placeableKey, true) }

            // attributes
            config.getStringList("$path.food.attributes").joinToString(";").takeIf { it.isNotBlank() }
                ?.let { pdc(key("attributes"), it) }

            // name
            config.getString("$path.itemName")?.let { name -> itemName(Text(name).component) }

            // set amount
            config.getInt("$path.amount").takeIf { it != 1 }?.let { itemStack.amount = it }

            config.getString("$path.weapon")?.let { value ->
                val parts = value.split("/")
                val damage = parts.getOrNull(0)?.toDoubleOrNull() ?: 2.0
                val speed = parts.getOrNull(1)?.toDoubleOrNull() ?: 1.6

                setWeapon(damage, speed)
            }

            // set glint
            if (config.getBoolean("$path.glint") == true) glint()

            if (config.getBoolean("$path.throwable") == true) throwable()

            config.getString("$path.potionColor")?.let { potion(it) }

            // makes sure to get the id directly from the texture servers!
            config.getString("$path.textures")?.let { id -> headTexture(id) }

            config.getString("$path.itemModel")?.let { id ->
                itemModel(id.lowercase())
            }

            // set custom model data
            config.getString("$path.customModelData")?.let { customModelData(if (it == "0") path else it) }

            //set lore
            lore(config.getStringList("$path.lore").map { Text(it).component })

            // stackSize needs to be 1-99 or else the server will explode (real)
            config.getInt("$path.stackSize").takeIf { it in 1..99 }?.let { stackSize(it) }

        }.build()
    }

    private fun food(config: FoxConfig, path: String, itemStack: ItemStack): ItemStack {
        if (!config.getKeys(path).contains("food")) return itemStack

        return FoodBuilder.builder(itemStack).apply {
            // i wonder why they split food into two components?
            hunger(
                config.getInt("$path.food.hunger") ?: 1,
                config.getDouble("$path.food.saturation")?.toFloat() ?: 2.0f
            )

            // always edible
            config.getBoolean("$path.food.isSnack")?.takeIf { it }?.let { isSnack() }

            // tp effect, similar to chorus fruit
            config.getDouble("$path.food.tp").takeIf { it != 0.0 }?.let { range -> tp(range.toFloat()) }

            // after eat sound (doesnt work for some reason)
            config.getString("$path.food.sound.burp")?.let { afterEatSound(it.lowercase()) }

            // sound while monchin and cronchin
            config.getString("$path.food.sound.eat")?.let { eatSound(it.lowercase()) }

            // should we show eat particles?
            config.getBoolean("$path.food.crumbs")?.takeIf { !it }?.let { noCrumbs() }

            // should it remove every effect?
            config.getBoolean("$path.food.isMilk")?.takeIf { it }?.let { clearEffects() }

            // add potion effects
            for (effectString in config.getStringList("$path.food.effects")) {
                val parts = effectString.split("/")
                // EFFECT/LEVEL/DURATION/CHANCE
                val effectName = parts[0].lowercase()
                val level = parts[1].toInt() - 1 // level 1 is actually level 2
                val chance = parts.getOrNull(3)?.toFloatOrNull() ?: 1f

                val effectType = Registry.POTION_EFFECT_TYPE[NamespacedKey.minecraft(effectName)] ?: continue

                addEffect(effectType, parts[2], level, chance)
            }

            // eating animation (i love this)
            config.getEnum(ItemUseAnimation::class.java, "$path.food.animation")?.let { animation(it) }

        }.build()
    }

    private fun equippable(config: FoxConfig, path: String, itemStack: ItemStack): ItemStack {
        if (!config.getKeys(path).contains("equippable")) return itemStack

        val ePath = "$path.equippable"
        val slot = config.getEnum(EquipmentSlot::class.java, "$ePath.slot") ?: itemStack.type.equipmentSlot

        val item = EquippableBuilder.builder(itemStack, slot).apply {
            config.getBoolean("$ePath.glider")?.let { glider() }
            config.getBoolean("$ePath.unbreakable")?.let { unbreakable() }
            config.getString("$ePath.overlay")?.let { cameraOverlay(NamespacedKey.fromString(it, null)!!) }
            config.getString("$ePath.model")?.let { model(NamespacedKey.fromString(it, null)!!) }
            config.getString("$ePath.sound")?.let { equipSound(it.lowercase()) }
        }.build()

        return item
    }
}