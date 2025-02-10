package co.akoot.plugins.plushies.listeners.tasks

import co.akoot.plugins.bluefox.api.FoxPlugin
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.Sound
import org.bukkit.attribute.Attribute
import org.bukkit.entity.*
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.bukkit.scheduler.BukkitRunnable
import org.joml.AxisAngle4f


class Throwable(private val shouldDrop: Boolean, private val snowBall: Snowball, private val display: ItemDisplay) : BukkitRunnable() {
    private var rotation = 0f

    override fun run() {
        val loc = snowBall.location

        if (!snowBall.isValid) {
            if (shouldDrop) loc.world.dropItemNaturally(loc.add(0.0, 0.5, 0.0), display.itemStack)
            loc.world.playSound(loc, Sound.ITEM_TRIDENT_HIT, 1f, 1f)
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
            val itemStack = player.inventory.itemInMainHand
            var shouldDrop = true

            if (player.gameMode != GameMode.CREATIVE) player.inventory.removeItem(itemStack)
            else shouldDrop = false

            val snowBall = player.launchProjectile(Snowball::class.java, player.eyeLocation.direction.multiply(2))

            val itemDisplay = snowBall.location.world.spawn(
                snowBall.location.add(0.0, 0.5, 0.0), ItemDisplay::class.java) { display: ItemDisplay ->

                display.apply {
                    setItemStack(itemStack)
                    itemDisplayTransform = ItemDisplay.ItemDisplayTransform.THIRDPERSON_RIGHTHAND
                    setRotation(player.yaw, 1.0f)
                }
            }

            // the players attack damage is changed depending on what weapon is in the main hand
            // set it to half a heart for normal items
            val damage = player.getAttribute(Attribute.ATTACK_DAMAGE)?.value?: 1.0

            snowBall.apply {
                // set the damage as pdc so we can check for it in the projectile hit event
                persistentDataContainer.set(axeKey, PersistentDataType.DOUBLE, damage)
                item = ItemStack(Material.AIR) // LOL what a beautiful hack
                addPassenger(itemDisplay) // this is much smoother than teleporting
            }

            player.location.world.playSound(player.location, Sound.ITEM_TRIDENT_THROW, 1f, 1f)

            Throwable(shouldDrop, snowBall, itemDisplay).runTaskTimer(plugin, 1L, 1L)
        }
    }
}