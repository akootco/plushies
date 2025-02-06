package co.akoot.plugins.plushies.listeners.tasks

import co.akoot.plugins.bluefox.api.FoxPlugin
import org.bukkit.GameMode
import org.bukkit.NamespacedKey
import org.bukkit.Sound
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Arrow
import org.bukkit.entity.ItemDisplay
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import org.joml.AxisAngle4f
import org.joml.Vector3f


class Throwable(private val shouldDrop: Boolean, private val display: ItemDisplay, private val arrow: Arrow) :
    BukkitRunnable() {

    private var rotation = 0f

    override fun run() {

        if (arrow.isInBlock || !arrow.isValid || !display.isValid) {
            if (shouldDrop) arrow.location.world.dropItemNaturally(arrow.location.add(0.0, 0.5, 0.0), display.itemStack)
            arrow.location.world.playSound(arrow.location, Sound.ITEM_TRIDENT_HIT, 1f, 1f)
            arrow.remove()
            display.remove()

            return cancel()
        }

        // spin animation
        rotation += 0.8f
        display.transformation = display.transformation.apply { // why the heck do i need the .apply
            leftRotation.set(AxisAngle4f(rotation, 1f, 0f, 0f))
        }
    }

    companion object {
        val axeKey = NamespacedKey("plushies", "throwable")

        fun spawnThrowable(player: Player, plugin: FoxPlugin) {
            val item = player.inventory.itemInMainHand
            var shouldDrop = true

            if (player.gameMode != GameMode.CREATIVE) player.inventory.removeItem(item)
            else shouldDrop = false

            // spawn the arrow at eye level
            val arrow = player.location.world.spawnArrow(player.eyeLocation, player.location.getDirection(), 3f, 0f)

            // sorry bedrock, but i hate armor stands!
            val itemDisplay = arrow.location.world.spawn(arrow.location.add(0.0,0.5,0.0), ItemDisplay::class.java) { display: ItemDisplay ->
                display.apply {
                    setItemStack(item)
                    itemDisplayTransform = ItemDisplay.ItemDisplayTransform.THIRDPERSON_RIGHTHAND
                    setRotation(player.location.yaw, 0f)
                    transformation = display.transformation.apply { // why the heck do i need the .apply v2
                        translation.set(Vector3f(0f, -0.4f, 0f)) // might not need this when i switch to teleporting
                    }
                }
            }

            // TODO
            // axe doesn't travel when arrow is hidden. will need to teleport instead of setting it as passenger
            arrow.apply {
                addPassenger(itemDisplay)
                shooter = player
                isSilent = true
                if (item.enchantments.containsKey(Enchantment.FIRE_ASPECT)) fireTicks = 999
            }

            Throwable(shouldDrop, itemDisplay, arrow).runTaskTimer(plugin, 1L, 1L)
        }

        // this sucks! dont use it!!!!!!!!!!!!!!!
//        fun getDamage(item: ItemStack): Float {
//            var damage: Double = when (item.type) {
//                Material.NETHERITE_AXE -> 10
//                Material.DIAMOND_AXE, Material.IRON_AXE, Material.STONE_AXE -> 9
//                Material.NETHERITE_SWORD -> 8
//                Material.GOLDEN_AXE, Material.WOODEN_AXE, Material.DIAMOND_SWORD -> 7
//                Material.NETHERITE_PICKAXE, Material.NETHERITE_SHOVEL, Material.MACE, Material.IRON_SWORD -> 6
//                Material.DIAMOND_SHOVEL -> 5.5
//                Material.STONE_SWORD, Material.IRON_PICKAXE, Material.IRON_SHOVEL -> 5
//                Material.WOODEN_SWORD, Material.GOLDEN_SWORD, Material.GOLDEN_PICKAXE, Material.WOODEN_PICKAXE -> 4
//                Material.STONE_PICKAXE, Material.STONE_SHOVEL -> 3
//                Material.WOODEN_SHOVEL, Material.GOLDEN_SHOVEL -> 2.5
//                else -> if (item.type.name.endsWith("_HOE")) 1.0 else 0.5
//            }
//
//            val sharpnessLevel = item.getEnchantmentLevel(Enchantment.SHARPNESS)
//            damage += 0.5 * sharpnessLevel
//            return damage.toFloat()
//        }
    }
}