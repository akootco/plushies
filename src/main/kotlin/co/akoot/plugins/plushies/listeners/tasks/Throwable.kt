package co.akoot.plugins.plushies.listeners.tasks

import co.akoot.plugins.bluefox.api.FoxPlugin
import co.akoot.plugins.bluefox.extensions.isSurventure
import co.akoot.plugins.bluefox.extensions.setPDC
import co.akoot.plugins.plushies.Plushies.Companion.key
import org.bukkit.Sound
import org.bukkit.attribute.Attribute
import org.bukkit.entity.*
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable
import org.joml.AxisAngle4f


class Throwable(private val shouldDrop: Boolean, private val snowBall: Snowball, private val display: ItemDisplay) : BukkitRunnable() {
    private var rotation = 0f

    override fun run() {
        val loc = snowBall.location

        if (!snowBall.isValid) {
            if (shouldDrop) loc.world.dropItemNaturally(loc.add(0.0, 0.5, 0.0), display.itemStack)
            loc.world.playSound(loc, Sound.ITEM_TRIDENT_HIT, 0.2f, 1f)
            display.remove()
            return cancel()
        }

        if (snowBall.ticksLived % 10 == 0) {
            display.location.world.playSound(
                display.location,
                Sound.ENTITY_PLAYER_ATTACK_SWEEP
                , 0.2f, 1f
            )
        }

        // spin animation
        rotation += 0.75f
        display.transformation = display.transformation.apply { // why the heck do i need the .apply
            leftRotation.set(AxisAngle4f(rotation, 1f, 0f, 0f))
        }
    }

    companion object {
        val axeKey = key("throwable")

        fun spawnThrowable(player: Player, plugin: FoxPlugin) {
            val itemStack = player.inventory.itemInMainHand
            val displayItem = itemStack.clone().asOne()
            var shouldDrop = true

            if (player.isSurventure) { itemStack.amount -= 1 }
            else { shouldDrop = false }

            val snowBall = player.launchProjectile(Snowball::class.java, player.eyeLocation.direction.multiply(2))

            val itemDisplay = snowBall.location.world.spawn(
                snowBall.location.add(0.0, 0.5, 0.0), ItemDisplay::class.java) { display: ItemDisplay ->

                display.apply {
                    setItemStack(displayItem)
                    itemDisplayTransform = ItemDisplay.ItemDisplayTransform.THIRDPERSON_RIGHTHAND
                    setRotation(player.yaw, 1.0f)
                }
            }

            // the players attack damage is changed depending on what weapon is in the main hand
            // set it to half a heart for normal items
            val damage = player.getAttribute(Attribute.ATTACK_DAMAGE)?.value?: 1.0

            snowBall.apply {
                // set the damage as pdc so we can check for it in the projectile hit event
                setPDC(axeKey, damage)
                item = ItemStack.empty() // LOL what a beautiful hack
                addPassenger(itemDisplay) // this is much smoother than teleporting
            }

            Throwable(shouldDrop, snowBall, itemDisplay).runTaskTimer(plugin, 1L, 1L)
        }
    }
}