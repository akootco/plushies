package co.akoot.plugins.plushies.listeners.handlers

import co.akoot.plugins.bluefox.extensions.getPDC
import co.akoot.plugins.bluefox.util.Text
import co.akoot.plugins.bluefox.util.runLater
import co.akoot.plugins.plushies.Plushies.Companion.key
import co.akoot.plugins.plushies.util.Items.itemKey
import co.akoot.plugins.plushies.util.builders.CraftRecipe
import co.akoot.plugins.plushies.util.builders.ItemBuilder
import io.papermc.paper.datacomponent.DataComponentTypes
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.title.Title
import org.bukkit.Material
import org.bukkit.Tag
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.RecipeChoice
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import kotlin.random.Random

val Player.isBoxing: Boolean
    get() = inventory.itemInMainHand.itemMeta?.getPDC<String>(itemKey) == "boxing_glove" &&
            inventory.itemInOffHand.itemMeta?.getPDC<String>(itemKey) == "boxing_glove"

val boxingGlove = ItemBuilder.builder(ItemStack(Material.LEATHER_HELMET, 2))
    .pdc(key("item"), "boxing_glove")
    .itemName(Text("Boxing Glove").component)
    .customModelData(2)
    .unsetData(DataComponentTypes.EQUIPPABLE)
    .unsetData(DataComponentTypes.ATTRIBUTE_MODIFIERS)
    .stackSize(2)
    .build()

val gloveRecipe = CraftRecipe.builder("boxing_glove", boxingGlove)
    .ingredient(RecipeChoice.MaterialChoice(Tag.WOOL))
    .ingredient(RecipeChoice.MaterialChoice(Material.LEATHER))
    .shapeless()

fun boxing(attacker: Player, target: Player, damage: Double) {
    val chance = Random.nextDouble()
    if (chance < 0.09 && target.health < 6) {
        target.showTitle(Title.title(Text("YOU GOT ROCKED!", NamedTextColor.RED).component, Component.empty()))
        target.showElderGuardian()
        target.addPotionEffects(
            listOf(
                PotionEffect(PotionEffectType.RESISTANCE, 200, 255),
                PotionEffect(PotionEffectType.WEAKNESS, 200, 5),
                PotionEffect(PotionEffectType.NAUSEA, 200, 5),
                PotionEffect(PotionEffectType.DARKNESS, 200, 5),
                PotionEffect(PotionEffectType.SLOWNESS, 200, 3)
            )
        )
    } else if (chance < 0.45) {
        runLater(15) {
            attacker.swingOffHand()
            target.damage(damage, attacker)
        }
    }
}