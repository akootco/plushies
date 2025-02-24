package co.akoot.plugins.plushies.listeners.tasks

import co.akoot.plugins.bluefox.extensions.getPDC
import co.akoot.plugins.bluefox.extensions.setPDC
import co.akoot.plugins.bluefox.util.Text
import co.akoot.plugins.bluefox.util.Text.Companion.invoke
import co.akoot.plugins.plushies.util.builders.ItemBuilder
import com.destroystokyo.paper.MaterialTags
import org.bukkit.*
import org.bukkit.attribute.Attribute
import org.bukkit.entity.*
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable
import kotlin.math.round
import kotlin.random.Random


class Golf(private val player: Player, private val golfBall: ArmorStand, private val startLocation: Location) :
    BukkitRunnable() {

    override fun run() {

        if (!golfBall.isValid) return cancel()

        val location = golfBall.location
        val distance = location.distance(startLocation)

        if (golfBall.isOnGround) {
            Text(player) {
                "Your golf ball traveled "("text")
                    .plus("${round(distance)}"("error_accent"))
                    .plus(" blocks!"("text"))
            }

            Text(player) { "${round(location.x)} ${round(location.y)} ${round(location.z)}"("accent") }
            return cancel()
        }
    }

    companion object {

        val golfKey = NamespacedKey("plushies", "golf")

        val golfBalls = mapOf(
            "white" to "c6e4f196e908a862353dcbb8edc69fc6b0f288f0e2c4bea72f09bff698753",
            "purple" to "a8be1dfc9b743ea838d5383c305a9aa5c5a403fb670fbb5210d4c143bc9bf026",
            "yellow" to "2a43879e6345620bc6811f7a28f31fbda986d2ab76e423e0a161321efb258b95",
            "pink" to "d1bff103165dd49408910264552493b2091b0b0be9dcdc2f9b1e3c4125e54b3c",
            "lime" to "3c9e7f09ae10f1a31a4a04fed55afb5ddee6c36c486fb233e3f8df2f0cd4325f",
            "blue" to "7bf5eab051ac69f416416bca3bc9d570c8d2544ce247ea7ff32da400e32255d",
            "red" to "632defbbc5d55b27ae719b7b7d6df0641e1145ab718b0a25c42bed3513089c60",
            "green" to "71850c25e8a0e04706c953b6b87eb2c19de6cc42148c7a605d80fa9fe6313fa3",
            "orange" to "bda5870af6b78237d6420b130d8bc3430d786e65a4e687dcc0df37063fc8c506"
        )

        fun spawnGolfBall(player: Player, location: Location): Boolean {

            val item = player.inventory.itemInMainHand
            val color = item.itemMeta.getPDC<String>(golfKey) ?: return false
            val armorStand = location.world.spawnEntity(location, EntityType.ARMOR_STAND) as ArmorStand

            // create the golf ball head
            val golfBall = ItemBuilder.builder(ItemStack(Material.PLAYER_HEAD))
                .headTexture(golfBalls[color] ?: "white")
                .build()

            // edit armor stand
            armorStand.apply {
                setPDC(golfKey, player.name)
                isVisible = false
                isSmall = true

                equipment.helmet = golfBall
                equipment.chestplate = item // store the original item in the chest

                for (slot in EquipmentSlot.entries) {
                    if (slot != EquipmentSlot.CHEST) { // lock all slots but the chest
                        addEquipmentLock(slot, ArmorStand.LockType.ADDING_OR_CHANGING)
                    }
                }
            }

            player.inventory.removeItem(item)

            return true
        }

        fun golfSwing(golfBall: ArmorStand, p: Player): Boolean {

            val owner = golfBall.getPDC<String>(golfKey)

            if (owner != p.name) {
                Text(p) { "this is $owner's golf ball"("error_accent") }
                return false // you cant hit someone else's ball, but you can still pick it up if needed.
            }

            // hit power is read from the items tooltip
            val hitPower = (p.getAttribute(Attribute.ATTACK_DAMAGE)?.value ?: 1.0) + 2.0

            // add randomness to the direction so every hit isnt the same.
            val direction = p.location.direction.multiply(hitPower + Random.nextDouble(0.8, 3.0))

            val item = p.inventory.itemInMainHand

            when {
                MaterialTags.SWORDS.isTagged(item.type) -> direction.setY(2) // power swing
                MaterialTags.HOES.isTagged(item.type) -> direction.setY(0.25) // putter
            }

            golfBall.velocity = direction
            return true
        }
    }
}