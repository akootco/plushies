package co.akoot.plugins.plushies.util

import co.akoot.plugins.bluefox.extensions.setPDC
import co.akoot.plugins.bluefox.util.text
import co.akoot.plugins.plushies.api.Interactable
import co.akoot.plugins.plushies.util.builders.ItemBuilder
import me.arcaniax.hdb.api.HeadDatabaseAPI
import net.kyori.adventure.text.Component
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.block.data.Directional
import org.bukkit.entity.Display.Brightness
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.Interaction
import org.bukkit.entity.ItemDisplay
import org.bukkit.entity.TextDisplay
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Transformation
import org.joml.AxisAngle4f
import org.joml.Vector3f

fun Entity.createHitbox(interactable: Interactable): Interaction {
    return world.spawn(location, Interaction::class.java) {
        it.interactionWidth = interactable.width
        it.interactionHeight = interactable.height
        it.setPDC(interactable.key, true)
    }.also { addPassenger(it) }
}

fun spawnTextDisplay(
    location: Location,
    text: Component,
    configure: TextDisplay.() -> Unit = {}
): TextDisplay {

    return (location.world.spawnEntity(
        location,
        EntityType.TEXT_DISPLAY
    ) as TextDisplay).apply {
        text(text)
        shadowRadius = 0f
        shadowStrength = 0f
        brightness = Brightness(5, 15)
        transformation = Transformation(
            Vector3f(),
            AxisAngle4f(),
            Vector3f(1f),
            AxisAngle4f()
        )

        configure()
    }
}

fun spawnItemDisplay(
    location: Location,
    item: ItemStack,
    configure: ItemDisplay.() -> Unit = {}
): ItemDisplay {
    val fixedYaw = (location.block.blockData as? Directional)?.facing?.let { facing ->
        when (facing) {
            BlockFace.EAST -> -90f
            BlockFace.WEST -> 90f
            BlockFace.SOUTH -> 0f
            else -> 180f
        }
    } ?: 180f

    return (location.world.spawnEntity(
        location.apply { this.yaw = fixedYaw },
        EntityType.ITEM_DISPLAY
    ) as ItemDisplay).apply {
        setItemStack(item.asOne())
        itemDisplayTransform = ItemDisplay.ItemDisplayTransform.HEAD
        shadowRadius = 0f
        shadowStrength = 0f
        brightness = Brightness(5, 15)
        transformation = Transformation(
            Vector3f(0f,0.5f,0f),
            AxisAngle4f(),
            Vector3f(1.001f),
            AxisAngle4f()
        )

        configure()
    }
}

fun createDisplay(location: Location, id: String, textured: Boolean = false) {
    val item = ItemBuilder.builder(
        if (textured) Material.OAK_PRESSURE_PLATE else Material.PLAYER_HEAD
    ).apply {
        if (textured) {
            val name = id.split("_").joinToString(" ") { it.replaceFirstChar { c -> c.titlecase() } }
            customModelData(id)
            itemModel("air")
            itemName(text(name))
        } else {
            val headItem = HeadDatabaseAPI().getItemHead(id)
            if (headItem != null) copyOf(headItem)
            else headTexture(id)
        }
    }.build()

    spawnItemDisplay(location, item)
}